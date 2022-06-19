#pragma once
#include <algorithm>
#include <cassert>
#include <cstddef>

template <typename T>
struct flexible_array_member {
  size_t ref_counter_;
  size_t capacity_;
  T data_[0];
};

template <typename T, size_t SMALL_SIZE>
struct socow_vector {
  using iterator = T*;
  using const_iterator = T const*;

  socow_vector() {}

  ~socow_vector() {
    if (is_dynamic) {
      destroy_dynamic_data();
    } else {
      destroy_static_data(0, size_);
    }
  }

  socow_vector(socow_vector const& other) {
    if (other.is_dynamic) {
      is_dynamic = other.is_dynamic;
      size_ = other.size_;
      storage = other.storage;
      ++storage->ref_counter_;
    } else {
      copy(static_data, other.static_data, 0, other.size_);
    }
  }

  socow_vector& operator=(const socow_vector& other) {
    if (this == &other) {
      return *this;
    }
    socow_vector tmp = socow_vector(other);
    this->swap(tmp);
    return *this;
  }

  T& operator[](size_t i) {
    return data()[i];
  }

  T const& operator[](size_t i) const {
    return data()[i];
  }

  T* data() {
    allocate_unique_memory(capacity());
    return is_dynamic ? storage->data_ : static_data;
  }

  T const* data() const {
    return is_dynamic ? storage->data_ : static_data;
  }

  size_t size() const {
    return size_;
  }

  T& front() {
    return data()[0];
  }

  T const& front() const {
    return data()[0];
  }

  T& back() {
    return data()[size_ - 1];
  }

  T const& back() const {
    return data()[size_ - 1];
  }

  void pop_back() {
    data()[--size_].~T();
  }

  bool empty() const {
    return size_ == 0;
  }

  size_t capacity() const {
    return is_dynamic ? storage->capacity_ : SMALL_SIZE;
  }

  void reserve(size_t new_cap) {
    allocate_unique_memory(std::max(new_cap, capacity()));
    if (new_cap > capacity()) {
      if (is_dynamic) {
        resize(new_cap, storage);
      } else {
        expand_static_data(new_cap);
      }
    }
  }

  void shrink_to_fit() {
    if (is_dynamic) {
      if (size_ < SMALL_SIZE) {
        fam tmp = storage;
        try {
          copy(static_data, tmp->data_, 0, size_);
        } catch (...) {
          storage = tmp;
          throw;
        }
        is_dynamic = false;
        destroy_dynamic_data(tmp);
      } else {
        allocate_unique_memory(capacity());
        if (size_ != storage->capacity_) {
          resize(size_, storage);
        }
      }
    }
  }

  void clear() {
    if (is_dynamic) {
      allocate_unique_memory(capacity());
      for (size_t i = size_; i > 0; i--) {
        storage->data_[i - 1].~T();
      }
    } else {
      destroy_static_data(0, size_);
    }
    size_ = 0;
  }

  void swap(socow_vector& other) {
    if (is_dynamic && other.is_dynamic) {
      std::swap(storage, other.storage);
    } else if (!is_dynamic && !other.is_dynamic) {
      for (size_t i = 0; i < std::min(size_, other.size_); i++) {
        std::swap(static_data[i], other.static_data[i]);
      }
      if (size_ < other.size_) {
        copy(static_data, other.static_data, size_, other.size_);
        other.destroy_static_data(size_, other.size_);
      } else {
        copy(other.static_data, static_data, other.size_, size_);
        destroy_static_data(other.size_, size_);
      }
    } else if (is_dynamic && !other.is_dynamic) {
      fam tmp = storage;
      try {
        copy(static_data, other.static_data, 0, other.size_);
      } catch (...) {
        storage = tmp;
        throw;
      }
      other.destroy_static_data(0, other.size_);
      other.storage = tmp;
    } else {
      other.swap(*this);
      return;
    }
    std::swap(size_, other.size_);
    std::swap(is_dynamic, other.is_dynamic);
  }

  iterator begin() {
    return data();
  }

  iterator end() {
    return data() + size_;
  }

  const_iterator begin() const {
    return data();
  }

  const_iterator end() const {
    return data() + size_;
  }

  void push_back(T const& val) {
    if (size_ + 1 <= SMALL_SIZE && !is_dynamic) {
      new (static_data + size_) T(val);
      size_++;
    } else if (!is_dynamic) {
      auto* tmp = allocate_fam(static_data, size_, 2 * SMALL_SIZE + 1);
      push_back(val, tmp);
      destroy_static_data(0, size_ - 1);
      storage = tmp;
      is_dynamic = true;
    } else {
      size_t new_cap =
          size_ == storage->capacity_ ? 2 * size_ + 1 : storage->capacity_;
      allocate_unique_memory(new_cap);
      push_back(val, storage);
    }
  }

  iterator insert(const_iterator pos, T const& val) {
    size_t it = pos - std::as_const(*this).begin();
    push_back(val);
    for (size_t i = size_; i > it + 1; i--) {
      std::swap(data()[i - 1], data()[i - 2]);
    }
    return begin() + it;
  }

  iterator erase(const_iterator pos) {
    return erase(pos, pos + 1);
  }

  iterator erase(const_iterator first, const_iterator last) {
    size_t start = first - std::as_const(*this).begin();
    size_t end = last - std::as_const(*this).begin();
    size_t len = end - start;
    allocate_unique_memory(capacity());
    for (size_t i = end; i < size_; i++) {
      std::swap(data()[i], data()[i - len]);
    }
    for (size_t i = size_; i > size_ - len; i--) {
      data()[i - 1].~T();
    }
    size_ -= len;
    return begin() + start;
  }

private:
  using fam = flexible_array_member<T>*;

  void destroy_dynamic_data(fam& data) {
    if (data->ref_counter_ > 1) {
      --data->ref_counter_;
    } else {
      destroy(data);
    }
  }

  void expand_static_data(size_t cap) {
    if (!is_dynamic) {
      auto* tmp = allocate_fam(static_data, size_, cap);
      destroy_static_data(0, size_);
      storage = tmp;
      is_dynamic = true;
    }
  }

  void allocate_unique_memory(size_t new_cap) {
    if (is_dynamic && storage->ref_counter_ > 1) {
      fam tmp = create_and_copy(storage, new_cap);
      --storage->ref_counter_;
      storage = tmp;
    }
  }

  void destroy_static_data(size_t from, size_t after) {
    for (size_t i = after; i > from; i--) {
      static_data[i - 1].~T();
    }
  }

  void destroy_dynamic_data() {
    if (storage->ref_counter_ > 1) {
      --storage->ref_counter_;
    } else {
      destroy(storage);
    }
  }

  fam allocate_fam(size_t size) {
    auto* buffer = static_cast<fam>(operator new(
        sizeof(flexible_array_member<T>) + sizeof(T) * size));
    new (buffer) flexible_array_member<T>{1, size};
    return buffer;
  }

  fam allocate_fam(const T* data, size_t size, size_t cap) {
    fam res = allocate_fam(cap);
    try {
      copy(res->data_, data, 0, size);
    } catch (...) {
      operator delete(res);
      throw;
    }
    return res;
  }

  void push_back(T const& val, fam& storage) {
    assert(storage->ref_counter_ == 1);
    if (size_ == storage->capacity_) {
      auto tmp_ = create_and_copy(storage, 2 * size_ + 1);
      try {
        new (tmp_->data_ + size_) T(val);
      } catch (...) {
        destroy_dynamic_data(tmp_);
        throw;
      }
      destroy(storage);
      storage = tmp_;
    } else {
      new (storage->data_ + size_) T(val);
    }
    size_++;
  }

  fam create_and_copy(const fam src, size_t cap) {
    auto* tmp_ = allocate_fam(cap);
    try {
      copy(tmp_->data_, src->data_, 0, size_);
    } catch (...) {
      operator delete(tmp_);
      throw;
    }
    return tmp_;
  }

  void resize(size_t cap, fam& storage) {
    auto tmp_ = create_and_copy(storage, cap);
    destroy(storage);
    storage = tmp_;
  }

  void destroy(fam& storage) {
    for (size_t i = size_; i > 0; i--) {
      storage->data_[i - 1].~T();
    }
    storage->~flexible_array_member<T>();
    operator delete(storage);
  }

  void copy(T* dst, const T* src, size_t from, size_t to) {
    std::uninitialized_copy(src + from, src + to, dst + from);
  }

  bool is_dynamic = false;
  size_t size_ = 0;

  union {
    T static_data[SMALL_SIZE];
    fam storage;
  };
};
