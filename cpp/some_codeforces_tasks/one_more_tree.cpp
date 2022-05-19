#include <bits/stdc++.h>
 
using namespace std;
 
struct RandomNumbers {
private:
    std::mt19937 source;
 
public:
    RandomNumbers() { // NOLINT
        source = std::mt19937(std::chrono::steady_clock::now().time_since_epoch().count());
    }
 
    int64_t getNumber(int64_t from, int64_t to) { // [from; to]
        return std::uniform_int_distribution<int64_t>(from, to)(source);
    }
 
    int64_t getNext() {
        return getNumber(0, LLONG_MAX);
    }
};
 
RandomNumbers randomNumbers; // NOLINT
 
struct node {
    int64_t x{}, y{};
    node *l{}, *r{};
    int64_t cnt{};
 
    // TODO
    bool inverse{};
    int64_t sum{};
    // TODO
 
    node() = default;
 
    explicit node(int64_t x) : x(x) {
        l = r = nullptr;
        cnt = 1;
 
        // TODO
        inverse = false;
        sum = x;
        // TODO
 
        y = randomNumbers.getNext();
    }
};
 
struct Tree {
private:
    static void merge(node *&root, node *l, node *r) { // NOLINT
        push(l);
        push(r);
        if (l == nullptr || r == nullptr) {
            root = l ? l : r;
        } else if (l->y > r->y) {
            merge(l->r, l->r, r);
            root = l;
        } else {
            merge(r->l, l, r->l);
            root = r;
        }
        update_cnt(root);
    }
 
private:
    static void split(node *root, node *&l, node *&r, int64_t x, int64_t add = 0) { // NOLINT
        if (root == nullptr) {
            return void(l = r = nullptr);
        }
        push(root);
        int64_t cur_x = add + getCnt(root->l);
        if (x <= cur_x) {
            split(root->l, l, root->l, x, add);
            r = root;
        } else {
            split(root->r, root->r, r, x, add + 1 + getCnt(root->l));
            l = root;
        }
        update_cnt(root);
    }
 
private:
    static void update_cnt(node *root) {
        if (root) {
            root->cnt = getCnt(root->l) + getCnt(root->r) + 1;
 
            // TODO
            root->sum = getSum(root->l) + getSum(root->r) + root->x;
            // TODO
        }
    }
 
private:
    static void push(node *root) {
        if (root && root->inverse) {
            root->inverse = false;
            std::swap(root->l, root->r);
            if (root->l) {
                root->l->inverse ^= true;
            }
            if (root->r) {
                root->r->inverse ^= true;
            }
            update_cnt(root);
        }
    }
 
private:
    static void output(node *root) { // NOLINT
        if (root == nullptr) {
            return;
        }
        push(root);
        output(root->l);
        std::cout << root->x << " ";
        output(root->r);
    }
 
public:
    static int64_t getSum(node *root) {
        return root ? root->sum : 0;
    }
 
public:
    static int64_t getCnt(node *root) {
        return root ? root->cnt : 0;
    }
 
public:
    static void outputTree(node *root) {
        output(root);
        std::cout << std::endl;
    }
 
public:
    // [l, r], from 0
    static void reverse(node *root, int64_t l, int64_t r) {
        node *t1, *t2, *t3;
        split(root, t1, t2, l);
        split(t2, t2, t3, r - l + 1);
        t2->inverse ^= true;
        merge(root, t1, t2);
        merge(root, root, t3);
        update_cnt(root);
    }
 
public:
    // inserts before pos, pos from 0
    static void insert(node *&root, node *vl, int64_t pos) {
        node *t1, *t2;
        split(root, t1, t2, pos);
        merge(t1, t1, vl);
        merge(t1, t1, t2);
        root = t1;
        update_cnt(root);
    }
 
public:
    // from 0
    static void erase(node *&root, int64_t pos) {
        node *t1, *t2, *t3, *t;
        split(root, t1, t2, pos);
        split(t2, t2, t3, pos + 1 - getCnt(t1));
        merge(t, t1, t3);
        root = t;
        update_cnt(root);
    }
 
public:
    // from 0
    static int64_t valAt(node *root, int64_t k) { // NOLINT
        if (getCnt(root->l) == k) {
            return root->x;
        }
        if (k <= getCnt(root->l) - 1) {
            return valAt(root->l, k);
        } else {
            return valAt(root->r, k - getCnt(root->l) - 1);
        }
    }
 
public:
    // from 0
    static void setValAt(node *&root, int64_t pos, int64_t val) {
        erase(root, pos);
        insert(root, new node(val), pos);
    }
 
public:
    // from 0, [l, r]
    static int64_t getSegmentSum(node *&root, int64_t l, int64_t r) {
        node *t1, *t2, *t3;
        split(root, t1, t2, l);
        split(t2, t2, t3, r - l + 1);
        int64_t ans = getSum(t2);
        merge(t1, t1, t2);
        merge(root, t1, t3);
        return ans;
    }
};
 
int main() {
    int n, m;
    cin >> n >> m;
    node *root = nullptr;
    for (int i = 0; i < n; i++) {
        int x;
        cin >> x;
        Tree::insert(root, new node(x), i);
    }
    for (int i = 0; i < m; i++) {
        string op;
        cin >> op;
        if (op == "add") {
            int pos, val;
            cin >> pos >> val;
            Tree::insert(root, new node(val), pos);
        } else if (op == "del") {
            int pos;
            cin >> pos;
            Tree::erase(root, pos-1);
        }
    }
    std::cout << Tree::getCnt(root) << std::endl;
    Tree::outputTree(root);
 
    return 0;
}