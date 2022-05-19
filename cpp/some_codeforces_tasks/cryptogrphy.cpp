#include <bits/stdc++.h>
 
using namespace std;
 
template<class T>
struct DOTree {
    size_t n;
    T defaultValue;
    vector<T> tree, a;
    vector<T> c;
    vector<bool> assignment;
 
    DOTree(size_t n, T defaultValue, const vector<T> &a) : n(n), defaultValue(defaultValue), a(a) {
        tree.resize(4 * n);
        c.resize(4 * n);
        assignment.assign(4 * n, false);
        build(0, 0, n);
    }
 
    T F(T left, T right) {
        return min(left, right);
    }
 
    void build(size_t v, size_t l, size_t r) {
        if (l == r - 1) {
            tree[v] = a[l];
            return;
        }
        size_t m = (l + r) / 2;
        build(2 * v + 1, l, m);
        build(2 * v + 2, m, r);
        tree[v] = F(tree[2 * v + 1], tree[2 * v + 2]);
    }
 
    void change(size_t v, size_t l, size_t r, size_t pos, T value) {
        if (l == r - 1) {
            tree[v] = value;
            return;
        }
        size_t m = (l + r) / 2;
        if (pos < m) {
            change(2 * v + 1, l, m, pos, value);
        } else {
            change(2 * v + 2, m, r, pos, value);
        }
        tree[v] = F(tree[2 * v + 1], tree[2 * v + 2]);
    }
 
    T get(size_t v, size_t l, size_t r, size_t askl, size_t askr) {
        if (l >= askr || askl >= r) {
            return defaultValue;
        }
        if (l >= askl && r <= askr) {
            return tree[v];
        }
        size_t m = (l + r) / 2;
        return F(get(2 * v + 1, l, m, askl, askr),
                 get(2 * v + 2, m, r, askl, askr));
    }
 
    void push(size_t v) {
        c[2 * v + 1] += c[v];
        c[2 * v + 2] += c[v];
        c[v] = 0;
    }
 
    void addSegment(size_t v, size_t l, size_t r, size_t askl, size_t askr, T value) {
        if (l >= askr || r <= askl) {
            return;
        }
        if (l >= askl && r <= askr) {
            c[v] += value;
            return;
        }
        push(v);
        size_t m = (l + r) / 2;
        addSegment(2 * v + 1, l, m, askl, askr, value);
        addSegment(2 * v + 2, m, r, askl, askr, value);
        tree[v] = min(tree[2 * v + 1] + c[2 * v + 1],
                      tree[2 * v + 2] + c[2 * v + 2]);
    }
 
    T getSegment(size_t v, size_t l, size_t r, size_t askl, size_t askr) {
        if (l >= askr || r <= askl) {
            return defaultValue;
        }
        if (l >= askl && r <= askr) {
            return tree[v] + c[v];
        }
        push(v);
        size_t m = (l + r) / 2;
        T left = getSegment(2 * v + 1, l, m, askl, askr);
        T right = getSegment(2 * v + 2, m, r, askl, askr);
        tree[v] = min(tree[2 * v + 1] + c[2 * v + 1],
                      tree[2 * v + 2] + c[2 * v + 2]);
        return F(left, right);
    }
 
    void pushAssignment(size_t v) {
        if (!assignment[v]) {
            return;
        }
        c[2 * v + 1] = c[v];
        c[2 * v + 2] = c[v];
        assignment[2 * v + 1] = true;
        assignment[2 * v + 2] = true;
        assignment[v] = false;
    }
 
    void setSegment(size_t v, size_t l, size_t r, size_t askl, size_t askr, T value) {
        if (l >= askr || r <= askl) {
            return;
        }
        if (l >= askl && r <= askr) {
            c[v] = value;
            assignment[v] = true;
            return;
        }
        pushAssignment(v);
        size_t m = (l + r) / 2;
        setSegment(2 * v + 1, l, m, askl, askr, value);
        setSegment(2 * v + 2, m, r, askl, askr, value);
        T left = assignment[2 * v + 1] ? c[2 * v + 1] : tree[2 * v + 1];
        T right = assignment[2 * v + 2] ? c[2 * v + 2] : tree[2 * v + 2];
        tree[v] = F(left, right);
    }
 
    T getSegmentAssignment(size_t v, size_t l, size_t r, size_t askl, size_t askr) {
        if (l >= askr || r <= askl) {
            return defaultValue;
        }
        if (l >= askl && r <= askr) {
            return assignment[v] ? c[v] : tree[v];
        }
        pushAssignment(v);
        size_t m = (l + r) / 2;
        T left = getSegmentAssignment(2 * v + 1, l, m, askl, askr);
        T right = getSegmentAssignment(2 * v + 2, m, r, askl, askr);
        tree[v] = F(assignment[2 * v + 1] ? c[2 * v + 1] : tree[2 * v + 1],
                    assignment[2 * v + 2] ? c[2 * v + 2] : tree[2 * v + 2]);
        return F(left, right);
    }
};
 
int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);
 
    // freopen(R"(C:\viajero\lab\input.txt)", "rt", stdin);
 
    size_t n, m;
    cin >> n >> m;
    vector<int64_t> a(n, 0);
    DOTree<int64_t> tree = DOTree(n, LLONG_MAX, a);
    for (size_t i = 0; i < m; i++) {
        int cmd;
        cin >> cmd;
        if (cmd == 1) {
            size_t l, r;
            int64_t v;
            cin >> l >> r >> v;
            tree.setSegment(0, 0, n, l, r, v);
        } else if (cmd == 2) {
            size_t l, r;
            cin >> l >> r;
            cout << tree.getSegmentAssignment(0, 0, n, l, r) << "\n";
        } else {
            assert(false);
        }
    }
 
    return 0;
}
