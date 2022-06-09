#pragma once
#include <cassert>
#include <cstdlib>
#include <iostream>
#include <iterator>
#include <utility>

template <typename T>
struct circular_buffer {
  template <bool IsConst>
  struct BaseIterator
      : public std::iterator<std::random_access_iterator_tag, T> {
    using iterator_category = std::bidirectional_iterator_tag;
    using difference_type = std::ptrdiff_t;
    using value_type = typename std::conditional<IsConst, T const, T>::type;
    using pointer = typename std::conditional<IsConst, T const*, T*>::type;
    using reference = typename std::conditional<IsConst, T const&, T&>::type;

    using buffer_type =
        typename std::conditional<IsConst, circular_buffer const,
                                  circular_buffer>::type*;

    template <bool IsConst_ = IsConst, class = std::enable_if_t<IsConst_>>
    BaseIterator(const BaseIterator<false>& other)
        : cur(other.cur), buffer(other.buffer) {}

    BaseIterator() = default;

    BaseIterator(BaseIterator const& other) = default;

    BaseIterator& operator+=(const size_t x) {
      cur += x;
      return *this;
    }

    BaseIterator& operator-=(const size_t x) {
      cur -= x;
      return *this;
    }

    friend BaseIterator operator+(BaseIterator const& first, size_t d) {
      BaseIterator tmp(first);
      tmp += d;
      return tmp;
    }

    friend BaseIterator operator+(size_t d, BaseIterator const& first) {
      BaseIterator tmp(first);
      tmp += d;
      return tmp;
    }

    friend ptrdiff_t operator-(BaseIterator const& first,
                               BaseIterator const& second) {
      ptrdiff_t a = first.cur;
      ptrdiff_t b = second.cur;
      return std::abs(a - b);
    }

    friend BaseIterator operator-(BaseIterator const& first, size_t d) {
      BaseIterator tmp(first);
      tmp -= d;
      return tmp;
    }

    T& operator[](size_t i) {
      return buffer->operator[](cur + i);
    }

    T const& operator[](size_t i) const {
      return buffer->operator[](cur + i);
    }

    friend bool operator==(BaseIterator const& a,
                           BaseIterator const& b) noexcept {
      return a.buffer == b.buffer && a.cur == b.cur;
    }

    friend bool operator!=(BaseIterator const& a,
                           BaseIterator const& b) noexcept {
      return !(a == b);
    }

    friend bool operator<(const BaseIterator& a, const BaseIterator& b) {
      return a.cur < b.cur;
    }

    friend bool operator>(const BaseIterator& a, const BaseIterator& b) {
      return !(a >= b);
    }

    friend bool operator<=(const BaseIterator& a, const BaseIterator& b) {
      return !(a > b);
    }

    friend bool operator>=(const BaseIterator& a, const BaseIterator& b) {
      return !(a < b);
    }

    // O(1) nothrow
    reference operator*() const {
      return buffer->operator[](cur);
    }

    // O(1) nothrow
    pointer operator->() const {
      return &buffer->operator[](cur);
    }

    // nothrow
    BaseIterator& operator++() & {
      cur++;
      return *this;
    }

    // nothrow
    BaseIterator operator++(int) & {
      BaseIterator ans = *this;
      cur++;
      return ans;
    }

    // nothrow
    BaseIterator& operator--() & {
      cur--;
      return *this;
    }

    // nothrow
    BaseIterator operator--(int) & {
      BaseIterator ans = *this;
      cur--;
      return ans;
    }

  private:
    size_t cur{};
    buffer_type buffer;

    BaseIterator(buffer_type buffer, size_t cur) : buffer(buffer), cur(cur) {}

    friend circular_buffer;
  };

  using iterator = BaseIterator<false>;
  using const_iterator = BaseIterator<true>;

  using reverse_iterator = std::reverse_iterator<iterator>;
  using const_reverse_iterator = std::reverse_iterator<const_iterator>;

  // O(1)
  circular_buffer() noexcept = default;

  // O(n), strong
  circular_buffer(circular_buffer const& other)
      : cap(other.cap), start(other.start), sz(other.sz),
        buffer(create_and_copy(other.buffer, cap)) {}

  // O(n)
  ~circular_buffer() {
    destroy(buffer);
    operator delete(buffer);
  }

  // O(n), strong
  circular_buffer& operator=(circular_buffer other) {
    if (this == &other) {
      return *this;
    }
    circular_buffer tmp = circular_buffer(other);
    this->swap(tmp);
    return *this;
  }

  // O(1)
  size_t size() const noexcept {
    return sz;
  }

  // O(1)
  T& operator[](size_t index) noexcept {
    return buffer[shift(start, index)];
  }

  // O(1)
  T const& operator[](size_t index) const noexcept {
    return buffer[shift(start, index)];
  }

  // O(1), nothrow
  bool empty() const noexcept {
    return sz == 0;
  }

  // O(n), nothrow
  void clear() noexcept {
    destroy(buffer);
    sz = cap = start = 0;
  }

  // O(1), strong
  void push_back(T const& val) {
    if (sz == cap) {
      put_into(val, sz);
      start = 0;
    } else {
      new (buffer + shift(start, sz)) T(val);
    }
    sz++;
  }

  // O(1), strong
  void push_front(T const& val) {
    if (sz == cap) {
      put_into(val, 2 * sz);
      start = cap - 1;
    } else {
      new (buffer + shift(start, -1)) T(val);
      start = shift(start, -1);
    }
    sz++;
  }

  // O(1)
  void pop_back() noexcept {
    back().~T();
    sz--;
  }

  // O(1)
  T& back() noexcept {
    return buffer[shift(start, sz - 1)];
  }

  // O(1)
  T const& back() const noexcept {
    return buffer[shift(start, sz - 1)];
  }

  // O(1)
  void pop_front() noexcept {
    buffer[start].~T();
    start = shift(start, 1);
    sz--;
  }

  // O(1)
  T& front() noexcept {
    return buffer[start];
  }

  // O(1)
  T const& front() const noexcept {
    return buffer[start];
  }

  // O(n), strong
  void reserve(size_t desired_capacity) {
    if (desired_capacity <= cap) {
      return;
    }
    T* tmp_ = create_and_copy(buffer, cap);
    destroy(buffer);
    operator delete(buffer);
    buffer = tmp_;
    cap = desired_capacity;
  }

  // O(1)
  size_t capacity() const noexcept {
    return cap;
  }

  // O(1)
  iterator begin() noexcept {
    return iterator(this, 0);
  }

  // O(1)
  const_iterator begin() const noexcept {
    return const_iterator(this, 0);
  }

  // O(1)
  iterator end() noexcept {
    return iterator(this, sz);
  }

  // O(1)
  const_iterator end() const noexcept {
    return const_iterator(this, sz);
  }

  // O(1)
  reverse_iterator rbegin() noexcept {
    return end();
  }

  // O(1)
  const_reverse_iterator rbegin() const noexcept {
    return end();
  }

  // O(1)
  reverse_iterator rend() noexcept {
    return begin();
  }

  // O(1)
  const_reverse_iterator rend() const noexcept {
    return begin();
  }

  // O(n), basic
  iterator insert(const_iterator pos, T const& val) {
    size_t it = pos - begin();
    if (it <= sz / 2) {
      push_back(val);
      for (size_t i = sz; i > it + 1; i--) {
        std::swap(buffer[shift(start, i - 1)], buffer[shift(start, i - 2)]);
      }
    } else {
      push_front(val);
      for (size_t i = 0; i + 1 <= it; i++) {
        std::swap(buffer[shift(start, i)], buffer[shift(start, i + 1)]);
      }
    }
    return begin() + it;
  }

  // O(n), basic
  iterator erase(const_iterator pos) {
    return erase(pos, pos + 1);
  }

  // O(n), basic
  iterator erase(const_iterator first, const_iterator last) {
    size_t st = first - begin();
    size_t end = last - begin();
    size_t len = end - st;
    if (circular_buffer::end() - last < first - begin()) {
      for (size_t i = end; i < sz; i++) {
        std::swap(buffer[shift(start, i)], buffer[shift(start, i - len)]);
      }
      for (size_t i = sz - len; i < sz; i++) {
        buffer[shift(start, i)].~T();
      }
      sz -= len;
    } else {
      for (size_t i = st; i > 0; i--) {
        std::swap(buffer[shift(start, i - 1)],
                  buffer[shift(start, i + len - 1)]);
      }
      for (size_t i = 0; i < len; i++) {
        pop_front();
      }
    }
    return begin() + st;
  }

  // O(1)
  void swap(circular_buffer& other) noexcept {
    std::swap(cap, other.cap);
    std::swap(buffer, other.buffer);
    std::swap(start, other.start);
    std::swap(sz, other.sz);
  }

private:
  size_t shift(size_t val, ptrdiff_t diff) const {
    return cap == 0 ? val : (val + cap + diff) % cap;
  }

  void destroy(T* data) {
    for (size_t i = sz; i > 0; i--) {
      data[shift(start, i - 1)].~T();
    }
  }

  void copy(T* dst, T* src) {
    size_t i = start;
    try {
      for (i = 0; i < sz; i++) {
        new (dst + i) T(src[shift(start, i)]);
      }
    } catch (...) {
      destroy(dst);
      throw;
    }
  }

  T* create_and_copy(T* src, size_t new_cap) {
    auto res = static_cast<T*>(operator new(sizeof(T) * new_cap));
    try {
      copy(res, src);
    } catch (...) {
      operator delete(res);
      throw;
    }
    return res;
  }

  void put_into(T const& val, size_t add) {
    T* tmp_ = create_and_copy(buffer, 2 * sz + 1);
    cap = 2 * sz + 1;
    try {
      new (tmp_ + add) T(val);
    } catch (...) {
      for (size_t i = sz; i > 0; i--) {
        tmp_[i - 1].~T();
      }
      operator delete(tmp_);
      throw;
    }
    destroy(buffer);
    operator delete(buffer);
    buffer = tmp_;
  }

private:
  size_t cap{0};
  size_t start{0};
  size_t sz{0};
  T* buffer{nullptr};
};
