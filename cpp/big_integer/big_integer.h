#pragma once

#include <cstddef>
#include <cstdint>
#include <functional>
#include <iosfwd>
#include <limits>
#include <string>
#include <utility>
#include <vector>

/*
 * Число храню в сис. с. с основанием MOD = uint32_t.
 * Для знака храню флаг sign_.
 * Для битовых операций написана функция complement для
 * перевода в дополнение до двух.
 */
struct big_integer {
  big_integer() = default;
  big_integer(big_integer const& other) = default;
  big_integer(int a) {
    init_signed(a);
  }
  big_integer(unsigned int a) {
    init_unsigned(a);
  }
  big_integer(long a) {
    init_signed(a);
  }
  big_integer(unsigned long a) {
    init_unsigned(a);
  }
  big_integer(long long a) {
    init_signed(a);
  }
  big_integer(unsigned long long a) {
    init_unsigned(a);
  }
  explicit big_integer(std::string const& str);
  ~big_integer() = default;

  big_integer& operator=(big_integer const& other);

  big_integer& operator+=(big_integer const& rhs);
  big_integer& operator-=(big_integer const& rhs);
  big_integer& operator*=(big_integer const& rhs);
  big_integer& operator/=(big_integer const& rhs);
  big_integer& operator%=(big_integer const& rhs);

  big_integer& operator&=(big_integer const& rhs);
  big_integer& operator|=(big_integer const& rhs);
  big_integer& operator^=(big_integer const& rhs);

  big_integer& operator<<=(int rhs);
  big_integer& operator>>=(int rhs);

  big_integer operator+() const;
  big_integer operator-() const;
  big_integer operator~() const;

  big_integer& operator++();
  big_integer operator++(int);

  big_integer& operator--();
  big_integer operator--(int);

  friend bool operator==(big_integer const& a, big_integer const& b);
  friend bool operator!=(big_integer const& a, big_integer const& b);
  friend bool operator<(big_integer const& a, big_integer const& b);
  friend bool operator>(big_integer const& a, big_integer const& b);
  friend bool operator<=(big_integer const& a, big_integer const& b);
  friend bool operator>=(big_integer const& a, big_integer const& b);

  friend std::string to_string(big_integer const& a);

private:
  enum class OP { ADD, SUB };

  static std::pair<big_integer, big_integer> normalize(const big_integer& u,
                                                       const big_integer& v,
                                                       ptrdiff_t n,
                                                       ptrdiff_t m);
  void swap(big_integer& other);
  static bool compare_with_same_sign(const big_integer& a, const big_integer& b,
                         int sign); // a * sign <? b * sign
  static uint32_t get(const big_integer& x, size_t pos);
  void init_unsigned(uint64_t x);
  void init_signed(int64_t x);
  static void mul_long_short(big_integer& a, uint32_t x);
  static void add_long_short(big_integer& a, uint32_t x);
  static uint32_t div_long_short(big_integer& a, uint32_t x);
  bool zero() const;
  void shrink_to_fit();
  template <typename T>
  static void binary_operation(big_integer& a, big_integer const& b, const T&);
  static void subtractOrAdd(const big_integer& src1, const big_integer& src2,
                            big_integer& dst, OP op);
  static void add(const big_integer& src1, const big_integer& src2,
                  big_integer& dst);
  static void invert(std::vector<uint32_t>& data);
  static void complement(big_integer& x);
  static int32_t nlz(uint32_t x);
  static std::pair<big_integer, big_integer>
  div_long_long(const big_integer& u, const big_integer& v);

  bool sign_{false}; // true <=> -; false <=> +
  std::vector<uint32_t> num_;
  const static uint64_t MOD =
      static_cast<uint64_t>(std::numeric_limits<uint32_t>::max()) + 1;
  const static uint32_t NUMBER_BITS = 32;
  const static uint32_t TEN = 10;
  const static uint32_t BLOCK = static_cast<uint32_t>(1E9);
  const static uint32_t DIGITS = 9;
  const static uint32_t ALL_ONES = (1ULL << 32) - 1;
};

big_integer operator+(big_integer a, big_integer const& b);
big_integer operator-(big_integer a, big_integer const& b);
big_integer operator*(big_integer a, big_integer const& b);
big_integer operator/(big_integer a, big_integer const& b);
big_integer operator%(big_integer a, big_integer const& b);

big_integer operator&(big_integer a, big_integer const& b);
big_integer operator|(big_integer a, big_integer const& b);
big_integer operator^(big_integer a, big_integer const& b);

big_integer operator<<(big_integer a, int b);
big_integer operator>>(big_integer a, int b);

bool operator==(big_integer const& a, big_integer const& b);
bool operator!=(big_integer const& a, big_integer const& b);
bool operator<(big_integer const& a, big_integer const& b);
bool operator>(big_integer const& a, big_integer const& b);
bool operator<=(big_integer const& a, big_integer const& b);
bool operator>=(big_integer const& a, big_integer const& b);

std::string to_string(big_integer const& a);
std::ostream& operator<<(std::ostream& s, big_integer const& a);
