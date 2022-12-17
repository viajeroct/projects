interface Shape : DimensionAware, SizeAware {
    fun clone(): DefaultShape
}

/**
 * Реализация Shape по умолчанию
 *
 * Должны работать вызовы DefaultShape(10), DefaultShape(12, 3), DefaultShape(12, 3, 12, 4, 56)
 * с любым количество параметров
 *
 * При попытке создать пустой Shape бросается EmptyShapeException
 *
 * При попытке указать неположительное число по любой размерности бросается NonPositiveDimensionException
 * Свойство index - минимальный индекс с некорректным значением, value - само значение
 *
 * Сама коллекция параметров недоступна, доступ - через методы интерфейса
 */
class DefaultShape(private vararg val dimensions: Int) : Shape {
    init {
        if (dimensions.isEmpty())
            throw ShapeArgumentException.EmptyShapeException()
        dimensions.withIndex().forEach { (index, value) ->
            if (value <= 0)
                throw ShapeArgumentException.NonPositiveDimensionException(index, value)
        }
    }

    override fun clone(): DefaultShape = DefaultShape(*dimensions)

    override val ndim = dimensions.size
    override fun dim(i: Int) = dimensions[i]
    override val size = dimensions.reduce { res, i -> res * i }
}

sealed class ShapeArgumentException(reason: String = "") : IllegalArgumentException(reason) {
    class EmptyShapeException : ShapeArgumentException("Shape is empty.")
    class NonPositiveDimensionException(index: Int, value: Int) :
        ShapeArgumentException("Non positive value $value at position $index.")
}
