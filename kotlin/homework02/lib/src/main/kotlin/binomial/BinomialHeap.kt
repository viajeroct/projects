package binomial

/*
 * BinomialHeap - реализация биномиальной кучи
 *
 * https://en.wikipedia.org/wiki/Binomial_heap
 *
 * Запрещено использовать
 *
 *  - var
 *  - циклы
 *  - стандартные коллекции
 *
 * Детали внутренней реализации должны быть спрятаны
 * Создание - только через single() и plus()
 *
 * Куча совсем без элементов не предусмотрена
 *
 * Операции
 *
 * plus с кучей
 * plus с элементом
 * top - взятие минимального элемента
 * drop - удаление минимального элемента
 */
class BinomialHeap<T : Comparable<T>> private constructor(private val trees: FList<BinomialTree<T>>) :
    SelfMergeable<BinomialHeap<T>> {

    companion object {
        fun <T : Comparable<T>> single(value: T) = BinomialHeap(flistOf(BinomialTree.single(value)))
    }

    // Дополнительная функция для самопроверки соблюдения
    // инварианта, используется только для тестов.
    private fun check(head: BinomialTree<T>, tail: FList<BinomialTree<T>>) {
        if (tail.isEmpty) return
        require(head.order < tail.top().order)
        check(tail.top(), tail.tail())
    }

    // Вызов функции для проверки инварианта.
    fun check() {
        if (trees.isEmpty) return
        check(trees.top(), trees.tail())
    }

    // Кол-во деревьев в списке, это тоже для самопроверки,
    // что список не раздувается больше log(n), используется
    // только в тестах.
    fun size() = trees.size

    // Слияние двух списков биномиальных деревьев, деревья одинакового
    // порядка не сливаются - O(log(n))
    private fun merge(a: FList<BinomialTree<T>>, b: FList<BinomialTree<T>>): FList<BinomialTree<T>> {
        return if (!a.isEmpty && !b.isEmpty) {
            if (a.top().order < b.top().order) FList.Cons(a.top(), merge(a.tail(), b))
            else FList.Cons(b.top(), merge(a, b.tail()))
        } else if (!a.isEmpty) a else b
    }

    // Слияние деревьев одинакового порядка для сжатия списка,
    // вызывать после каждого вызова функции merge - O(log(n))
    private fun compress(head: BinomialTree<T>, rest: FList<BinomialTree<T>>): FList<BinomialTree<T>> {
        if (rest.isEmpty) return flistOf(head)
        val cur = rest.top()
        return if (head.order == cur.order) {
            if (rest.tail().isEmpty || rest.tail().top().order != cur.order)
                compress(head + cur, rest.tail())
            else FList.Cons(head, compress(cur + rest.tail().top(), rest.tail().tail()))
        } else FList.Cons(head, compress(cur, rest.tail()))
    }

    /*
     * Слияние куч
     *
     * Требуемая сложность - O(log(n))
     */
    override fun plus(other: BinomialHeap<T>): BinomialHeap<T> {
        val merged = merge(trees, other.trees)
        return BinomialHeap(compress(merged.top(), merged.tail()))
    }

    /*
     * Добавление элемента
     *
     * Требуемая сложность - O(log(n))
     */
    operator fun plus(elem: T) = plus(single(elem))

    /*
     * Минимальный элемент
     *
     * Требуемая сложность - O(log(n))
     */
    fun top() = trees.fold(trees.top().value) { x, y -> minOf(x, y.value) }

    /*
     * Удаление элемента
     *
     * Требуемая сложность - O(log(n))
     */
    fun drop(): BinomialHeap<T> {
        val minimum = trees.fold(trees.top()) { x, y -> if (x.value < y.value) x else y }
        return BinomialHeap(trees.remove(minimum)) + BinomialHeap(minimum.children.reverse())
    }
}
