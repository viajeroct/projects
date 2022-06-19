#include <cassert>
#include <iterator>
#include <utility>

template <typename T>
struct set {
  struct tree_node {
    friend struct set;

  private:
    T key;
    tree_node* left{nullptr};
    tree_node* right{nullptr};
    tree_node* parent{nullptr};

    explicit tree_node(T key) : key(key) {}
  };

  using node_ptr = tree_node*;

  struct iterator {
    friend struct set;

    using iterator_category = std::bidirectional_iterator_tag;
    using difference_type = std::ptrdiff_t;
    using value_type = T const;
    using pointer = T const*;
    using reference = T const&;

    iterator() = default;

    friend bool operator==(iterator const& a, iterator const& b) noexcept {
      return a.ptr == b.ptr && a.root == b.root;
    }

    friend bool operator!=(iterator const& a, iterator const& b) noexcept {
      return !(a == b);
    }

    // O(1) nothrow
    reference operator*() const {
      return ptr->key;
    }

    // O(1) nothrow
    pointer operator->() const {
      return &ptr->key;
    }

    // nothrow
    iterator& operator++() & {
      ptr = next(ptr);
      return *this;
    }

    // nothrow
    iterator operator++(int) & {
      iterator ans = *this;
      ++*this;
      return ans;
    }

    // nothrow
    iterator& operator--() & {
      ptr = ptr == nullptr ? find_biggest(root) : prev(ptr);
      return *this;
    }

    // nothrow
    iterator operator--(int) & {
      iterator ans = *this;
      --*this;
      return ans;
    }

  private:
    iterator(node_ptr ptr, node_ptr root) : ptr(ptr), root(root) {}

    node_ptr ptr{nullptr};
    node_ptr root{nullptr};
  };

  using const_iterator = iterator;
  using reverse_iterator = std::reverse_iterator<iterator>;
  using const_reverse_iterator = std::reverse_iterator<const_iterator>;

  // O(1) nothrow
  set() = default;

  // O(n) strong
  set(set const& other) : root(make_tree_copy(other.root)) {}

  // O(n) strong
  set& operator=(set const& other) {
    if (root == other.root) {
      return *this;
    }
    set tmp = other;
    swap(tmp);
    return *this;
  }

  // O(n) nothrow
  ~set() {
    destroy_tree(root);
  };

  // O(n) nothrow
  void clear() {
    destroy_tree(root);
    root = nullptr;
  }

  // O(1) nothrow
  bool empty() {
    return root == nullptr;
  }

  // nothrow
  const_iterator begin() const {
    return {find_smallest(root), root};
  }

  // nothrow
  const_iterator end() const {
    return {nullptr, root};
  }

  // nothrow
  const_reverse_iterator rbegin() const {
    return std::reverse_iterator(end());
  }

  // nothrow
  const_reverse_iterator rend() const {
    return std::reverse_iterator(begin());
  }

  // O(h) nothrow
  iterator erase(iterator it) {
    auto nxt = upper_bound(it.ptr->key);
    root = erase(root, it.ptr);
    delete it.ptr;
    return nxt;
  }

  // O(h) strong
  const_iterator find(T const& key) const {
    auto res = find(root, key);
    return res != nullptr ? const_iterator(res, root) : end();
  }

  // O(h) strong
  const_iterator lower_bound(T const& key) const {
    return some_bound([](T x, T y) { return x >= y; }, key);
  }

  // O(h) strong
  const_iterator upper_bound(T const& key) const {
    return some_bound([](T x, T y) { return x > y; }, key);
  }

  // O(1) nothrow
  void swap(set& other) {
    std::swap(root, other.root);
  }

  // O(h) strong
  std::pair<iterator, bool> insert(T const& key) {
    if (find(key) != iterator(nullptr, root)) {
      return {{nullptr, root}, false};
    }
    node_ptr inserted = nullptr;
    root = insert(root, key, inserted);
    return {{inserted, root}, true};
  }

private:
  node_ptr make_tree_copy(node_ptr node) {
    if (!node) {
      return nullptr;
    }
    auto cur = new tree_node(node->key);
    auto res = make_tree_copy(node->left);
    if (res != nullptr) {
      res->parent = cur;
    }
    cur->left = res;

    res = make_tree_copy(node->right);
    if (res != nullptr) {
      res->parent = cur;
    }
    cur->right = res;
    return cur;
  }

  node_ptr find(node_ptr node, const T& key) const {
    if (!node) {
      return nullptr;
    }
    if (node->key == key) {
      return node;
    }
    auto left = find(node->left, key);
    return left != nullptr ? left : find(node->right, key);
  }

  void destroy_tree(node_ptr node) {
    if (node != nullptr) {
      destroy_tree(node->left);
      destroy_tree(node->right);
      delete node;
    }
  }

  node_ptr erase(node_ptr node, node_ptr v) {
    node_ptr p = v->parent;
    if (v->left == nullptr && v->right == nullptr) {
      if (p == nullptr) {
        return nullptr;
      }
      (p->left == v ? p->left : p->right) = nullptr;
    } else if (v->left == nullptr || v->right == nullptr) {
      auto tmp = v->left == nullptr ? v->right : v->left;
      if (p == nullptr) {
        tmp->parent = nullptr;
        return tmp;
      }
      (p->left == v ? p->left : p->right) = tmp;
      tmp->parent = p;
    } else {
      node_ptr s = find_smallest(v->right);
      if (v->parent == nullptr) {
        node = s;
      } else {
        (v->parent->left == v ? v->parent->left : v->parent->right) = s;
      }
      (s->parent->left == s ? s->parent->left : s->parent->right) = s->right;
      if (s->right != nullptr) {
        s->right->parent = s->parent;
      }
      s->left = v->left;
      if (s->left != nullptr) {
        s->left->parent = s;
      }
      s->right = v->right;
      if (s->right != nullptr) {
        s->right->parent = s;
      }
      s->parent = v->parent;
    }
    return node;
  }

  static node_ptr next(node_ptr x) {
    if (x->right != nullptr) {
      return find_smallest(x->right);
    }
    auto y = x->parent;
    while (y != nullptr && x == y->right) {
      x = y;
      y = y->parent;
    }
    return y;
  }

  static node_ptr prev(node_ptr x) {
    if (x->left != nullptr) {
      return find_biggest(x->left);
    }
    auto y = x->parent;
    while (y != nullptr && x == y->left) {
      x = y;
      y = y->parent;
    }
    return y;
  }

  node_ptr insert(node_ptr node, T const& key, node_ptr& inserted) {
    if (node == nullptr) {
      return (inserted = new tree_node(key));
    }
    if (key < node->key) {
      auto res = insert(node->left, key, inserted);
      res->parent = node;
      node->left = res;
    } else {
      node_ptr res = insert(node->right, key, inserted);
      res->parent = node;
      node->right = res;
    }
    return node;
  }

  static node_ptr find_smallest(node_ptr node) {
    node_ptr cur = node;
    while (cur != nullptr && cur->left) {
      cur = cur->left;
    }
    return cur;
  }

  static node_ptr find_biggest(node_ptr node) {
    node_ptr cur = node;
    while (cur != nullptr && cur->right) {
      cur = cur->right;
    }
    return cur;
  }

  template <typename F>
  const_iterator some_bound(const F& f, T const& key) const {
    node_ptr cur = root, ans = nullptr;
    while (cur != nullptr) {
      if (f(cur->key, key)) {
        ans = cur;
        cur = cur->left;
      } else {
        cur = cur->right;
      }
    }
    return {ans, root};
  }

  node_ptr root{nullptr};
};
