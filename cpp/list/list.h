#pragma once
#include <cassert>
#include <iterator>

template <typename T>
class list {
private:
  struct base_node {
    base_node() = default;
    base_node(base_node* prev, base_node* next) : prev(prev), next(next) {}

    base_node* prev = nullptr;
    base_node* next = nullptr;
  };

  struct node : base_node {
    T obj;

    node(base_node* prev, base_node* next, const T& obj)
        : base_node(prev, next), obj(obj) {}
  };

  template <bool is_const>
  struct BaseIterator {
    using iterator_category = std::bidirectional_iterator_tag;
    using difference_type = std::ptrdiff_t;
    using value_type = typename std::conditional<is_const, T const, T>::type;
    using pointer = typename std::conditional<is_const, T const*, T*>::type;
    using reference = typename std::conditional<is_const, T const&, T&>::type;

    template <bool is_const_ = is_const, class = std::enable_if_t<is_const_>>
    BaseIterator(const BaseIterator<false>& other) : data(other.data) {}

    BaseIterator() = default;
    BaseIterator(BaseIterator const& other) = default;

    friend bool operator==(BaseIterator const& a,
                           BaseIterator const& b) noexcept {
      return a.data == b.data;
    }

    friend bool operator!=(BaseIterator const& a,
                           BaseIterator const& b) noexcept {
      return !(a == b);
    }

    // O(1) nothrow
    reference operator*() const {
      return static_cast<node_type>(data)->obj;
    }

    // O(1) nothrow
    pointer operator->() const {
      return &static_cast<node_type>(data)->obj;
    }

    BaseIterator& operator=(const BaseIterator& other) {
      data = other.data;
      return *this;
    }

    // nothrow
    BaseIterator& operator++() & {
      data = data->next;
      return *this;
    }

    // nothrow
    BaseIterator operator++(int) & {
      BaseIterator ans = *this;
      data = data->next;
      return ans;
    }

    // nothrow
    BaseIterator& operator--() & {
      data = data->prev;
      return *this;
    }

    // nothrow
    BaseIterator operator--(int) & {
      BaseIterator ans = *this;
      data = data->prev;
      return ans;
    }

  private:
    using base_node_type =
        typename std::conditional<is_const, base_node const, base_node>::type*;
    using node_type =
        typename std::conditional<is_const, node const, node>::type*;

    base_node_type data;

    explicit BaseIterator(base_node_type buffer) : data(buffer) {}

    friend list;
  };

  base_node fake;

public:
  // bidirectional iterator
  using iterator = BaseIterator<false>;
  // bidirectional iterator
  using const_iterator = BaseIterator<true>;
  using reverse_iterator = std::reverse_iterator<iterator>;
  using const_reverse_iterator = std::reverse_iterator<const_iterator>;

  // O(1)
  list() noexcept : fake(&fake, &fake) {}

  // O(n), strong
  list(list const& other) : list() {
    for (auto it = other.begin(); it != other.end(); it++) {
      push_back(*it);
    }
  }

  // O(n), strong
  list& operator=(list const& other) {
    if (this == &other) {
      return *this;
    }
    auto tmp = list(other);
    swap(*this, tmp);
    return *this;
  }

  // O(n)
  ~list() {
    clear();
  }

  // O(1)
  bool empty() const noexcept {
    return fake.next == &fake && fake.prev == &fake;
  }

  // O(1)
  T& front() noexcept {
    return static_cast<node*>(fake.next)->obj;
  }

  // O(1)
  T const& front() const noexcept {
    return static_cast<node* const>(fake.next)->obj;
  }

  // O(1), strong
  void push_front(T const& val) {
    insert(begin(), val);
  }

  // O(1)
  void pop_front() noexcept {
    erase(begin());
  }

  // O(1)
  T& back() noexcept {
    return static_cast<node*>(fake.prev)->obj;
  }

  // O(1)
  T const& back() const noexcept {
    return static_cast<node* const>(fake.prev)->obj;
  }

  // O(1), strong
  void push_back(T const& val) {
    insert(end(), val);
  }

  // O(1)
  void pop_back() noexcept {
    auto tmp = end();
    erase(--tmp);
  }

  // O(1)
  iterator begin() noexcept {
    return iterator(fake.next);
  }

  // O(1)
  const_iterator begin() const noexcept {
    return const_iterator(fake.next);
  }

  // O(1)
  iterator end() noexcept {
    return iterator(&fake);
  }

  // O(1)
  const_iterator end() const noexcept {
    return const_iterator(&fake);
  }

  // O(1)
  reverse_iterator rbegin() noexcept {
    return reverse_iterator(end());
  }

  // O(1)
  const_reverse_iterator rbegin() const noexcept {
    return const_reverse_iterator(end());
  }

  // O(1)
  reverse_iterator rend() noexcept {
    return reverse_iterator(begin());
  }

  // O(1)
  const_reverse_iterator rend() const noexcept {
    return const_reverse_iterator(begin());
  }

  // O(n)
  void clear() noexcept {
    if (empty()) {
      return;
    }
    for (auto cur = fake.next; cur != &fake;) {
      auto nxt = cur->next;
      delete static_cast<node*>(cur);
      cur = nxt;
    }
    fake.next = fake.prev = &fake;
  }

  // O(1), strong
  iterator insert(const_iterator pos, T const& val) {
    node* to_insert =
        new node(pos.data->prev, const_cast<base_node*>(pos.data), val);
    to_insert->prev->next = to_insert;
    to_insert->next->prev = to_insert;
    return iterator(to_insert);
  }

  // O(1)
  iterator erase(const_iterator pos) noexcept {
    auto tmp = pos;
    ++tmp;
    return erase(pos, tmp);
  }

  // O(n)
  iterator erase(const_iterator first, const_iterator last) noexcept {
    auto first_alive = first.data->prev;
    auto last_alive = const_cast<base_node*>(last.data);
    for (auto it = first.data; it != last.data;) {
      auto* cur = const_cast<base_node*>(it);
      it = it->next;
      delete static_cast<node*>(cur);
    }
    first_alive->next = last_alive;
    last_alive->prev = first_alive;
    return iterator(last_alive);
  }

  void link(base_node* u, base_node* v) {
    u->prev = v;
    v->next = u;
  }

  // O(1)
  void splice(const_iterator pos_, list& other, const_iterator first_,
              const_iterator last_) noexcept {
    if (first_ == last_) {
      return;
    }
    auto first_buffer = const_cast<base_node*>(first_.data);
    auto last_buffer = const_cast<base_node*>(last_.data);
    auto pos_buffer = const_cast<base_node*>(pos_.data);
    node* before_first = static_cast<node*>(first_buffer->prev);
    link(first_buffer, pos_buffer->prev);
    link(pos_buffer, last_buffer->prev);
    link(last_buffer, before_first);
  }

  friend void swap(list& a, list& b) noexcept {
    if (!a.empty() || !b.empty()) {
      if (a.empty() && !b.empty()) {
        a.fake = b.fake;
        a.fake.next->prev = a.fake.prev->next = &a.fake;
        b.fake.next = b.fake.prev = &b.fake;
      } else if (!a.empty() && b.empty()) {
        swap(b, a);
      } else {
        std::swap(a.fake, b.fake);
        a.fake.next->prev = a.fake.prev->next = &a.fake;
        b.fake.next->prev = b.fake.prev->next = &b.fake;
      }
    }
  }
};
