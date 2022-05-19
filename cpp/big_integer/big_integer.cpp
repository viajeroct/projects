#include "big_integer.h"
#include <algorithm>
#include <ostream>
#include <stdexcept>

void big_integer::swap(big_integer& other) {
  std::swap(num_, other.num_);
  std::swap(sign_, other.sign_);
}

big_integer& big_integer::operator=(const big_integer& other) {
  if (this == &other) {
    return *this;
  }
  big_integer tmp = big_integer(other);
  swap(tmp);
  shrink_to_fit();
  return *this;
}

void big_integer::init_signed(int64_t x) {
  sign_ = x < 0;
  uint64_t y = x;
  if (x < 0) {
    y = static_cast<uint64_t>(-(x + 1)) + 1;
  }
  init_unsigned(y);
}

void big_integer::init_unsigned(uint64_t x) {
  if (x == 0) {
    return;
  }
  num_.push_back(x & ALL_ONES);
  if (x >= MOD) {
    num_.push_back(x >> NUMBER_BITS);
  }
  shrink_to_fit();
}

big_integer::big_integer(std::string const& str) {
  if (str.empty()) {
    throw std::invalid_argument("Error: number is empty.");
  }
  if (str.front() == '-' && str.size() == 1) {
    throw std::invalid_argument("Error: consists only of '-'.");
  }
  size_t start = str.front() == '-' ? 1 : 0;
  sign_ = start;
  uint32_t tmp = 0, cur_pow = 1;
  for (size_t i = start; i < str.length(); i++) {
    if (!('0' <= str[i] && str[i] <= '9')) {
      throw std::invalid_argument("Error: not a number.");
    }
    tmp = tmp * TEN + (str[i] - '0');
    cur_pow *= TEN;
    if (cur_pow == BLOCK) {
      mul_long_short(*this, BLOCK);
      add_long_short(*this, tmp);
      tmp = 0;
      cur_pow = 1;
    }
  }
  if (cur_pow != 1) {
    mul_long_short(*this, cur_pow);
    add_long_short(*this, tmp);
  }
  shrink_to_fit();
}

void big_integer::subtractOrAdd(const big_integer& src1,
                                const big_integer& src2, big_integer& dst,
                                OP op) {
  uint64_t carry = 0;
  size_t size = std::max(src1.num_.size(), src2.num_.size());
  dst.num_.resize(size);
  for (size_t i = 0; i < size; i++) {
    uint32_t other = get(src2, i);
    uint32_t now = get(src1, i);
    uint64_t cur = other + carry;
    if (op == OP::ADD) {
      carry = (cur + now) / MOD;
      dst.num_[i] = (cur + now) % MOD;
    } else {
      carry = now < cur;
      dst.num_[i] = carry * MOD + now - cur;
    }
  }
  if (carry != 0) {
    dst.num_.push_back(carry);
  }
  dst.shrink_to_fit();
}

bool big_integer::compare_with_same_sign(const big_integer& a,
                                         const big_integer& b, int sign) {
  if (a.num_.size() * sign < b.num_.size() * sign) {
    return true;
  } else if (a.num_.size() * sign > b.num_.size() * sign) {
    return false;
  }
  size_t pos = a.num_.size();
  while (pos > 0) {
    if (a.num_[pos - 1] * sign < b.num_[pos - 1] * sign) {
      return true;
    } else if (a.num_[pos - 1] * sign > b.num_[pos - 1] * sign) {
      return false;
    }
    pos--;
  }
  return false;
}

void big_integer::add(const big_integer& src1, const big_integer& src2,
                      big_integer& dst) {
  if (src1.sign_ == src2.sign_) {
    subtractOrAdd(src1, src2, dst, OP::ADD);
    dst.sign_ = src1.sign_;
  } else if (compare_with_same_sign(src2, src1, 1)) {
    subtractOrAdd(src1, src2, dst, OP::SUB);
    dst.sign_ = src1.sign_;
  } else {
    subtractOrAdd(src2, src1, dst, OP::SUB);
    dst.sign_ = src2.sign_;
  }
}

big_integer& big_integer::operator+=(big_integer const& rhs) {
  add(*this, rhs, *this);
  shrink_to_fit();
  return *this;
}

big_integer& big_integer::operator-=(big_integer const& rhs) {
  sign_ ^= true;
  add(rhs, *this, *this);
  sign_ ^= true;
  shrink_to_fit();
  return *this;
}

big_integer& big_integer::operator*=(big_integer const& rhs) {
  big_integer ans;
  ans.num_.resize(num_.size() + rhs.num_.size());
  for (size_t i = 0; i < num_.size(); i++) {
    uint64_t carry = 0;
    for (size_t j = 0; j < rhs.num_.size() || carry != 0; j++) {
      uint32_t other = get(rhs, j);
      carry += num_[i] * 1ULL * other + ans.num_[i + j];
      ans.num_[i + j] = carry % MOD;
      carry /= MOD;
    }
  }
  ans.sign_ = sign_ ^ rhs.sign_;
  swap(ans);
  shrink_to_fit();
  return *this;
}

big_integer& big_integer::operator&=(big_integer const& rhs) {
  binary_operation(*this, rhs, [](uint32_t x, uint32_t y) { return x & y; });
  return *this;
}

big_integer& big_integer::operator|=(big_integer const& rhs) {
  binary_operation(*this, rhs, [](uint32_t x, uint32_t y) { return x | y; });
  return *this;
}

big_integer& big_integer::operator^=(big_integer const& rhs) {
  binary_operation(*this, rhs, [](uint32_t x, uint32_t y) { return x ^ y; });
  return *this;
}

big_integer& big_integer::operator<<=(int rhs) {
  if (this->zero()) {
    return *this;
  }
  size_t i = num_.size();
  num_.resize(num_.size() + rhs / NUMBER_BITS + 1);
  uint32_t part1 = rhs % NUMBER_BITS;
  uint32_t part2 = NUMBER_BITS - part1;
  size_t j = num_.size() - 1;
  while (i != 0) {
    num_[j--] = ((num_[i] << part1) | (num_[i - 1] >> part2));
    i--;
  }
  num_[j] = num_[i] << part1;
  std::fill(num_.begin(), num_.begin() + static_cast<int>(j), 0);
  shrink_to_fit();
  return *this;
}

big_integer& big_integer::operator>>=(int rhs) {
  if (this->zero()) {
    return *this;
  }
  uint32_t part1 = rhs % NUMBER_BITS;
  uint32_t part2 = NUMBER_BITS - part1;
  size_t i = 0;
  for (size_t j = rhs / NUMBER_BITS; j < num_.size(); j++, i++) {
    uint32_t other = j + 1 < num_.size() ? (num_[j + 1] << part2) : 0;
    num_[i] = ((num_[j] >> part1) | other);
  }
  num_.resize(i);
  if (sign_)
    *this -= 1;
  shrink_to_fit();
  return *this;
}

big_integer big_integer::operator+() const {
  return *this;
}

big_integer big_integer::operator-() const {
  big_integer res = *this;
  res.sign_ ^= true;
  return res;
}

void big_integer::invert(std::vector<uint32_t>& data) {
  std::for_each(data.begin(), data.end(), [](uint32_t& x) { x = ~x; });
}

void big_integer::complement(big_integer& x) {
  if (x.sign_) {
    invert(x.num_);
    add_long_short(x, 1);
  }
  x.shrink_to_fit();
}

big_integer big_integer::operator~() const {
  big_integer tmp = *this;
  complement(tmp);
  invert(tmp.num_);
  tmp.sign_ ^= true;
  complement(tmp);
  tmp.shrink_to_fit();
  return tmp;
}

big_integer& big_integer::operator++() {
  *this += 1;
  shrink_to_fit();
  return *this;
}

big_integer big_integer::operator++(int) {
  big_integer tmp = *this;
  *this += 1;
  tmp.shrink_to_fit();
  return tmp;
}

big_integer& big_integer::operator--() {
  *this -= 1;
  shrink_to_fit();
  return *this;
}

big_integer big_integer::operator--(int) {
  big_integer tmp = *this;
  *this -= 1;
  tmp.shrink_to_fit();
  return tmp;
}

big_integer operator+(big_integer a, big_integer const& b) {
  return a += b;
}

big_integer operator-(big_integer a, big_integer const& b) {
  return a -= b;
}

big_integer operator*(big_integer a, big_integer const& b) {
  return a *= b;
}

big_integer operator/(big_integer a, big_integer const& b) {
  return a /= b;
}

big_integer operator%(big_integer a, big_integer const& b) {
  return a %= b;
}

big_integer operator&(big_integer a, big_integer const& b) {
  return a &= b;
}

big_integer operator|(big_integer a, big_integer const& b) {
  return a |= b;
}

big_integer operator^(big_integer a, big_integer const& b) {
  return a ^= b;
}

big_integer operator<<(big_integer a, int b) {
  return a <<= b;
}

big_integer operator>>(big_integer a, int b) {
  return a >>= b;
}

bool operator==(big_integer const& a, big_integer const& b) {
  if (a.num_.size() != b.num_.size()) {
    return false;
  }
  for (size_t i = 0; i < a.num_.size(); i++) {
    if (a.num_[i] != b.num_[i]) {
      return false;
    }
  }
  return true;
}

bool operator!=(big_integer const& a, big_integer const& b) {
  return !(a == b);
}

bool operator<(big_integer const& a, big_integer const& b) {
  if (a.sign_ && !b.sign_) {
    return true;
  } else if (!a.sign_ && b.sign_) {
    return false;
  } else {
    return big_integer::compare_with_same_sign(a, b, a.sign_ ? -1 : 1);
  }
}

bool operator>(big_integer const& a, big_integer const& b) {
  return b < a;
}

bool operator<=(big_integer const& a, big_integer const& b) {
  return !(a > b);
}

bool operator>=(big_integer const& a, big_integer const& b) {
  return !(a < b);
}

std::string to_string(big_integer const& a) {
  big_integer tmp = big_integer(a);
  std::string ans;
  while (!tmp.zero()) {
    uint32_t rem = big_integer::div_long_short(tmp, big_integer::BLOCK);
    std::string cur = std::to_string(rem);
    std::reverse(cur.begin(), cur.end());
    cur.resize(big_integer::DIGITS, '0');
    ans += cur;
  }
  while (ans.size() > 1 && ans.back() == '0') {
    ans.pop_back();
  }
  if (a.sign_ && !a.zero()) {
    ans += "-";
  }
  std::reverse(ans.begin(), ans.end());
  if (ans.empty()) {
    ans = "0";
  }
  return ans;
}

void big_integer::mul_long_short(big_integer& a, uint32_t x) {
  uint64_t carry = 0;
  for (uint32_t& block : a.num_) {
    uint64_t cur = block * 1ULL * x + carry;
    block = cur % MOD;
    carry = cur / MOD;
  }
  if (carry > 0)
    a.num_.push_back(carry);
  a.shrink_to_fit();
}

void big_integer::add_long_short(big_integer& a, uint32_t x) {
  uint64_t carry = x;
  for (uint32_t& block : a.num_) {
    uint64_t cur = block + carry;
    block = cur % MOD;
    carry = cur / MOD;
    if (carry == 0)
      break;
  }
  if (carry > 0)
    a.num_.push_back(carry);
  a.shrink_to_fit();
}

uint32_t big_integer::div_long_short(big_integer& a, uint32_t x) {
  uint64_t carry = 0;
  for (size_t i = a.num_.size(); i != 0; i--) {
    uint64_t cur = carry * MOD + a.num_[i - 1];
    a.num_[i - 1] = cur / x;
    carry = cur % x;
  }
  a.shrink_to_fit();
  return carry;
}

bool big_integer::zero() const {
  return num_.empty();
}

template <typename T>
void big_integer::binary_operation(big_integer& a, const big_integer& b,
                                   const T& f) {
  big_integer tmp = b;
  complement(a);
  complement(tmp);
  while (a.num_.size() < tmp.num_.size()) {
    a.num_.push_back(a.sign_ * ALL_ONES);
  }
  while (tmp.num_.size() < a.num_.size()) {
    tmp.num_.push_back(tmp.sign_ * ALL_ONES);
  }
  for (size_t i = 0; i < a.num_.size(); i++) {
    a.num_[i] = f(a.num_[i], tmp.num_[i]);
  }
  a.sign_ = f(a.sign_, tmp.sign_);
  if (a.sign_) {
    complement(a);
  }
  a.shrink_to_fit();
}

void big_integer::shrink_to_fit() {
  while (!num_.empty() && num_.back() == 0) {
    num_.pop_back();
  }
}

std::ostream& operator<<(std::ostream& s, big_integer const& a) {
  return s << to_string(a);
}

/*
 * Возвращает кол-во ведущих нулевых битов числа.
 */
int32_t big_integer::nlz(uint32_t x) {
  int32_t ans = 0;
  for (int i = NUMBER_BITS - 1; i >= 0 && ((x >> i) & 1) == 0; i--, ans++) {}
  return ans;
}

/*
 * Нормализация путем сдвига v влево, такого, что
 * старший бит становится единичным.
 */
std::pair<big_integer, big_integer> big_integer::normalize(const big_integer& u,
                                                           const big_integer& v,
                                                           ptrdiff_t n,
                                                           ptrdiff_t m) {
  big_integer un, vn;
  un.num_.resize(m + 1);
  vn.num_.resize(n);
  const int32_t s = nlz(v.num_[n - 1]);
  const int32_t s_32 = (int)NUMBER_BITS - s;
  for (ptrdiff_t i = n - 1; i > 0; i--) {
    vn.num_[i] = (v.num_[i] << s) | ((uint64_t)(v.num_[i - 1]) >> s_32);
  }
  vn.num_[0] = v.num_[0] << s;
  un.num_[m] = (uint64_t)(u.num_[m - 1]) >> s_32;
  for (ptrdiff_t i = m - 1; i > 0; i--)
    un.num_[i] = (u.num_[i] << s) | ((uint64_t)(u.num_[i - 1]) >> s_32);
  un.num_[0] = u.num_[0] << s;
  return {un, vn};
}

/*
 * Книга Дональда Кнута "Искусство программирования".
 * В ней описан алгоритм деления в главе 4, стр. 302, Алгоритм D.
 *
 * Более подробно он описан в книге
 * Уоррена Генри "Алгоритмические трюки для программистов"
 * стр 212, пункт 9.1.
 *
 * Асимптотика алгоритма O(nm), что также написано
 * в книге Кнута.
 *
 * q - частное, r - остаток, отдельно рассмотрены случаи, где
 * m < n или sizeof(v) == 32.
 */
std::pair<big_integer, big_integer>
big_integer::div_long_long(const big_integer& u, const big_integer& v) {
  auto m = static_cast<ptrdiff_t>(u.num_.size());
  auto n = static_cast<ptrdiff_t>(v.num_.size());
  big_integer q, r;
  q.num_.resize(m);
  r.num_.resize(n);
  if (m < n) {
    return {0, u};
  }
  if (n == 1) {
    q = u;
    div_long_short(q, v.num_.back());
    subtractOrAdd(u, q * v, r, OP::SUB);
    r.sign_ = false;
    return {q, r};
  }
  const int32_t s = nlz(v.num_[n - 1]);
  const int32_t s_32 = (int)NUMBER_BITS - s;
  auto res = normalize(u, v, n, m);
  big_integer& un = res.first;
  big_integer& vn = res.second;
  for (ptrdiff_t j = m - n; j >= 0; j--) {
    /*
     * qhat - предполагаемая цифра частного. Это главный цикл
     * и вычисление оценки q[j].
     */
    uint64_t qhat =
        (un.num_[j + n] * MOD + un.num_[j + n - 1]) / vn.num_[n - 1];
    uint64_t rhat =
        (un.num_[j + n] * MOD + un.num_[j + n - 1]) - qhat * vn.num_[n - 1];
    while (true) {
      if (qhat >= MOD ||
          qhat * vn.num_[n - 2] > MOD * rhat + un.num_[j + n - 2]) {
        qhat--;
        rhat += vn.num_[n - 1];
        if (rhat < MOD)
          continue;
      }
      break;
    }
    uint64_t k = 0;
    int64_t t = 0;
    for (size_t i = 0; i < n; i++) {
      uint64_t p = qhat * 1ULL * vn.num_[i];
      t = static_cast<int64_t>(un.num_[i + j] - k - (p & ALL_ONES));
      un.num_[i + j] = t;
      k = (p >> NUMBER_BITS) - (t >> NUMBER_BITS);
    }
    t = static_cast<int64_t>(un.num_[j + n] - k);
    un.num_[j + n] = t;
    q.num_[j] = qhat;
    if (t < 0) {
      q.num_[j]--;
      k = 0;
      for (size_t i = 0; i < n; i++) {
        t = static_cast<int64_t>(un.num_[i + j] + vn.num_[i] + k);
        un.num_[i + j] = t;
        k = t >> NUMBER_BITS;
      }
      un.num_[j + n] += k;
    }
  }
  /*
   * Денормализация остатка.
   */
  for (size_t i = 0; i < n; i++) {
    r.num_[i] = (un.num_[i] >> s) | ((uint64_t)(un.num_[i + 1]) << s_32);
  }
  return {q, r};
}

big_integer& big_integer::operator/=(big_integer const& rhs) {
  auto res = div_long_long(*this, rhs);
  bool was = sign_ ^ rhs.sign_;
  swap(res.first);
  sign_ = was;
  shrink_to_fit();
  return *this;
}

big_integer& big_integer::operator%=(big_integer const& rhs) {
  auto res = div_long_long(*this, rhs);
  bool was = sign_;
  swap(res.second);
  sign_ = !this->zero() && was;
  shrink_to_fit();
  return *this;
}

uint32_t big_integer::get(const big_integer& x, size_t pos) {
  return pos < x.num_.size() ? x.num_[pos] : 0;
}
