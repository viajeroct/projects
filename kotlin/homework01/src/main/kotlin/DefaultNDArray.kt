interface NDArray : SizeAware, DimensionAware {
    /*
     * Получаем значение по индексу point
     *
     * Если размерность point не равна размерности NDArray
     * бросаем IllegalPointDimensionException
     *
     * Если позиция по любой из размерностей некорректна с точки зрения
     * размерности NDArray, бросаем IllegalPointCoordinateException
     */
    fun at(point: Point): Int

    /*
     * Устанавливаем значение по индексу point
     *
     * Если размерность point не равна размерности NDArray
     * бросаем IllegalPointDimensionException
     *
     * Если позиция по любой из размерностей некорректна с точки зрения
     * размерности NDArray, бросаем IllegalPointCoordinateException
     */
    fun set(point: Point, value: Int)

    /*
     * Копируем текущий NDArray
     *
     */
    fun copy(): NDArray

    /*
     * Создаем view для текущего NDArray
     *
     * Ожидается, что будет создан новая реализация  интерфейса.
     * Но она не должна быть видна в коде, использующем эту библиотеку как внешний артефакт
     *
     * Должна быть возможность делать view над view.
     *
     * In-place-изменения над view любого порядка видна в оригинале и во всех view
     *
     * Проблемы thread-safety игнорируем
     */
    fun view(): NDArray

    /*
     * In-place сложение
     *
     * Размерность other либо идентична текущей, либо на 1 меньше
     * Если она на 1 меньше, то по всем позициям, кроме "лишней", она должна совпадать
     *
     * Если размерности совпадают, то делаем поэлементное сложение
     *
     * Если размерность other на 1 меньше, то для каждой позиции последней размерности мы
     * делаем поэлементное сложение
     *
     * Например, если размерность this - (10, 3), а размерность other - (10), то мы для три раза прибавим
     * other к каждому срезу последней размерности
     *
     * Аналогично, если размерность this - (10, 3, 5), а размерность other - (10, 3), то мы пять раз прибавим
     * other к каждому срезу последней размерности
     */
    fun add(other: NDArray)

    /*
     * Умножение матриц. Immutable-операция. Возвращаем NDArray
     *
     * Требования к размерности - как для умножения матриц.
     *
     * this - обязательно двумерна
     *
     * other - может быть двумерной, с подходящей размерностью, равной 1 или просто вектором
     *
     * Возвращаем новую матрицу (NDArray размерности 2)
     *
     */
    fun dot(other: NDArray): NDArray
}

/*
 * Базовая реализация NDArray
 *
 * Конструкторы должны быть недоступны клиенту
 *
 * Инициализация - через factory-методы ones(shape: Shape), zeros(shape: Shape) и метод copy
 */
class DefaultNDArray private constructor(
    private val shape: Shape,
    private val data: IntArray
) : NDArray {
    private constructor(shape: Shape, initValue: Int) :
            this(shape, IntArray(shape.size) { initValue })

    companion object {
        fun zeros(shape: Shape) = DefaultNDArray(shape, 0)
        fun ones(shape: Shape) = DefaultNDArray(shape, 1)
    }

    private fun getArrayIndex(point: Point): Int {
        if (point.ndim != ndim)
            throw NDArrayException.IllegalPointDimensionException(ndim, point.ndim)
        var index = 0
        var cur = shape.size
        for (i in 0 until point.ndim) {
            if (point.dim(i) < 0 || point.dim(i) >= dim(i))
                throw NDArrayException.IllegalPointCoordinateException(i, 0, dim(i) - 1)
            cur /= dim(i)
            index += cur * point.dim(i)
        }
        return index
    }

    override fun at(point: Point): Int = data[getArrayIndex(point)]

    override fun set(point: Point, value: Int) {
        data[getArrayIndex(point)] = value
    }

    override fun copy(): NDArray = DefaultNDArray(shape.clone(), data.copyOf())

    override fun view(): NDArray = ViewDefaultNDArray(this)

    private fun getPointByIndex(index: Int, diff: Int): Point {
        var curIndex = index
        var prod = size / if (diff == 0) 1 else dim(ndim - 1)
        val res = IntArray(ndim - diff) { 0 }
        for (i in 0 until ndim - diff) {
            prod /= dim(i)
            res[i] = curIndex / prod
            curIndex -= prod * res[i]
        }
        return DefaultPoint(*res)
    }

    override fun add(other: NDArray) {
        if (ndim != other.ndim && ndim != other.ndim + 1)
            throw NDArrayException.IllegalPointAddDimensionException(ndim, other.ndim)
        for (i in 0 until other.ndim)
            if (dim(i) != other.dim(i))
                throw NDArrayException.IllegalDimensionException(i, other.dim(i), dim(i))
        val interval = if (ndim == other.ndim) 1 else dim(ndim - 1)
        for (i in 0 until size)
            data[i] += other.at(getPointByIndex(i / interval, ndim - other.ndim))
    }

    override fun dot(other: NDArray): NDArray {
        if (ndim != 2)
            throw NDArrayException.IllegalDotOperation("A must be a matrix.")
        if (other.ndim == 0 || other.ndim >= 3)
            throw NDArrayException.IllegalDotOperation("B must be a matrix or vector.")
        val b: NDArray
        if (other.ndim == 2) b = other
        else {
            b = zeros(DefaultShape(other.dim(0), 1))
            b.add(other)
        }
        if (dim(1) != b.dim(0))
            throw NDArrayException.IllegalDotOperation("Incompatible dimensions.")
        val res = zeros(DefaultShape(dim(0), b.dim(1)))
        for (i in 0 until res.dim(0))
            for (j in 0 until res.dim(1))
                for (k in 0 until b.dim(0))
                    res.set(
                        DefaultPoint(i, j),
                        res.at(DefaultPoint(i, j)) + at(DefaultPoint(i, k)) * b.at(DefaultPoint(k, j))
                    )
        return res
    }

    override val size: Int get() = shape.size
    override val ndim: Int get() = shape.ndim
    override fun dim(i: Int) = shape.dim(i)
}

class ViewDefaultNDArray(private val arr: NDArray) : NDArray by arr

sealed class NDArrayException(reason: String = "") : Exception(reason) {
    class IllegalPointCoordinateException(index: Int, min: Int, max: Int) :
        NDArrayException("Index should be in [$min, $max], but got $index.")

    class IllegalPointDimensionException(needDim: Int, curDim: Int) :
        NDArrayException("Dimension is $curDim, but expected $needDim.")

    class IllegalPointAddDimensionException(needDim: Int, curDim: Int) :
        NDArrayException("Dimension is $curDim, but expected $needDim (or ${needDim - 1}).")

    class IllegalDotOperation(reason: String = "") : NDArrayException(reason)

    class IllegalDimensionException(i: Int, value: Int, need: Int) :
        NDArrayException("Expected value $need at dimension $i, but got $value.")
}
