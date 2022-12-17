import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class MyTests {
    @Test
    fun testAdd() {
        val a = DefaultNDArray.ones(DefaultShape(5, 7, 8, 2))
        val b = DefaultNDArray.ones(DefaultShape(5, 7, 8))
        b.set(DefaultPoint(4, 3, 6), -100)
        b.set(DefaultPoint(1, 6, 2), 200)
        a.add(b)
        for (k in 0 until 2)
            for (i in 0 until 5)
                for (j in 0 until 7)
                    for (l in 0 until 8) {
                        var need = if (i == 4 && j == 3 && l == 6) -99 else 2
                        if (i == 1 && j == 6 && l == 2) need = 201
                        assertEquals(need, a.at(DefaultPoint(i, j, l, k)))
                    }
    }

    @Test
    fun testDot() {
        val a = DefaultNDArray.ones(DefaultShape(2, 3))
        a.set(DefaultPoint(0, 0), -9)
        a.set(DefaultPoint(0, 1), 10)
        a.set(DefaultPoint(0, 2), 2)
        a.set(DefaultPoint(1, 0), -7)
        a.set(DefaultPoint(1, 1), 6)
        a.set(DefaultPoint(1, 2), 4)
        val b = DefaultNDArray.ones(DefaultShape(3, 2))
        b.set(DefaultPoint(0, 0), 4)
        b.set(DefaultPoint(0, 1), -1)
        b.set(DefaultPoint(1, 0), 3)
        b.set(DefaultPoint(1, 1), -7)
        b.set(DefaultPoint(2, 0), -12)
        b.set(DefaultPoint(2, 1), 3)
        val c = a.dot(b)
        assertEquals(-30, c.at(DefaultPoint(0, 0)))
        assertEquals(-55, c.at(DefaultPoint(0, 1)))
        assertEquals(-58, c.at(DefaultPoint(1, 0)))
        assertEquals(-23, c.at(DefaultPoint(1, 1)))
    }

    @Test
    fun testDot2() {
        val a = DefaultNDArray.ones(DefaultShape(2, 3))
        a.set(DefaultPoint(0, 0), -9)
        a.set(DefaultPoint(0, 1), 10)
        a.set(DefaultPoint(0, 2), 2)
        a.set(DefaultPoint(1, 0), -7)
        a.set(DefaultPoint(1, 1), 6)
        a.set(DefaultPoint(1, 2), 4)
        val b = DefaultNDArray.ones(DefaultShape(3))
        b.set(DefaultPoint(0), 4)
        b.set(DefaultPoint(1), 3)
        b.set(DefaultPoint(2), -12)
        val c = a.dot(b)
        assertEquals(-30, c.at(DefaultPoint(0, 0)))
        assertEquals(-58, c.at(DefaultPoint(1, 0)))
    }

    @Test
    fun testView() {
        val a = DefaultNDArray.ones(DefaultShape(2, 4, 6))
        val b = a.view()
        val c = b.view()
        b.set(DefaultPoint(1, 3, 5), 100)
        assertEquals(100, a.at(DefaultPoint(1, 3, 5)))
        assertEquals(100, b.at(DefaultPoint(1, 3, 5)))
        assertEquals(100, c.at(DefaultPoint(1, 3, 5)))
        val d = c.copy()
        val e = a.view()
        c.set(DefaultPoint(1, 1, 1), -100)
        assertEquals(-100, a.at(DefaultPoint(1, 1, 1)))
        assertEquals(-100, b.at(DefaultPoint(1, 1, 1)))
        assertEquals(-100, c.at(DefaultPoint(1, 1, 1)))
        assertEquals(-100, e.at(DefaultPoint(1, 1, 1)))
        assertEquals(1, d.at(DefaultPoint(1, 1, 1)))
        assertEquals(100, d.at(DefaultPoint(1, 3, 5)))
    }

    @Test
    fun testView2() {
        val a = DefaultNDArray.ones(DefaultShape(1, 1))
        val b = a.view()
        a.add(b)
        assertEquals(2, a.at(DefaultPoint(0, 0)))
        assertEquals(2, b.at(DefaultPoint(0, 0)))
    }
}
