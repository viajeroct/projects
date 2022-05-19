#include <bits/stdc++.h>
 
using namespace std;
 
const int MAX_TREE_SIZE = 45000000 / 2;
 
struct Node {
    int lft{}, rgt{}, ttl{};
 
    Node() = default;
 
    explicit Node(int value) : lft(0), rgt(0), ttl(value) {}
};
 
Node tree[MAX_TREE_SIZE];
int modulo = 1E9;
const int MAX_ARRAY_SIZE = 450101;
int counter, n, k, m;
static int dt[MAX_ARRAY_SIZE], roots[MAX_ARRAY_SIZE], incr[MAX_ARRAY_SIZE];
 
int build(int l, int r) {
    int v = ++counter;
    if (l == r) {
        tree[v] = Node(0);
    } else {
        int middle = (l + r) / 2;
        tree[v].lft = build(l, middle);
        tree[v].rgt = build(middle + 1, r);
        tree[v].ttl = tree[tree[v].lft].ttl + tree[tree[v].rgt].ttl;
    }
    return v;
}
 
int get_sum(int v, int l, int r, int tl, int tr) {
    if (tl > r || tr < l) {
        return 0;
    }
    if (tl >= l && tr <= r) {
        return tree[v].ttl;
    }
    int middle = (tl + tr) / 2;
    return get_sum(tree[v].lft, l, r, tl, middle) + get_sum(tree[v].rgt, l, r, middle + 1, tr);
}
 
int modify(int v, int pos, int value, int tl, int tr) {
    int nxt = counter++;
    if (tl == tr) {
        tree[nxt] = Node(value);
    } else {
        int mid = (tl + tr) / 2;
        if (pos <= mid) {
            tree[nxt].rgt = tree[v].rgt;
            tree[nxt].lft = modify(tree[v].lft, pos, value, tl, mid);
        } else {
            tree[nxt].lft = tree[v].lft;
            tree[nxt].rgt = modify(tree[v].rgt, pos, value, mid + 1, tr);
        }
        tree[nxt].ttl = tree[tree[nxt].lft].ttl + tree[tree[nxt].rgt].ttl;
    }
    return nxt;
}
 
int get_k(int p, int q, int stat, int tl, int tr) {
    if (tl == tr) {
        return tl;
    }
    int middle = (tl + tr) / 2;
    int tmp = tree[tree[q].lft].ttl - tree[tree[p].lft].ttl;
    if (stat <= tmp) {
        return get_k(tree[p].lft, tree[q].lft, stat, tl, middle);
    } else {
        return get_k(tree[p].rgt, tree[q].rgt, stat - tmp, middle + 1, tr);
    }
}
 
int main() {
    cin >> n;
    int a1, l;
    cin >> a1 >> l >> m;
    dt[1] = a1;
    incr[1] = dt[1];
    for (int i = 2; i <= n; i++) {
        dt[i] = (int) ((dt[i - 1] * 1LL * l + m) % modulo);
        incr[i] = dt[i];
    }
    sort(incr + 1, incr + n + 1);
    for (int i = 1; i <= n; i++) {
        dt[i] = (int) (lower_bound(incr + 1, incr + n + 1, dt[i]) - incr);
    }
    roots[k++] = build(1, n);
    for (int i = 1; i <= n; i++) {
        int val = get_sum(roots[k - 1], dt[i], dt[i], 1, n);
        roots[k] = modify(roots[k - 1], dt[i], val + 1, 1, n);
        k++;
    }
    int64_t result = 0;
    int b;
    cin >> b;
    for (int test = 0; test < b; test++) {
        int g;
        cin >> g;
        int x1, lx, mx;
        cin >> x1 >> lx >> mx;
        int y1, ly, my;
        cin >> y1 >> ly >> my;
        int k1, lk, mk;
        cin >> k1 >> lk >> mk;
 
        int xg1, yg1, ig1, jg1, kg1;
 
        xg1 = x1;
        yg1 = y1;
        ig1 = min(xg1, yg1);
        jg1 = max(xg1, yg1);
        kg1 = k1;
        int ans = incr[get_k(roots[ig1 - 1], roots[jg1], kg1, 1, n)];
        result += ans;
        for (int i = 2; i <= g; i++) {
            int xg2, yg2, ig2, jg2, kg2;
            xg2 = (int) ((((ig1 - 1) * 1LL * lx + mx) % n) + 1);
            yg2 = (int) ((((jg1 - 1) * 1LL * ly + my) % n) + 1);
            ig2 = min(xg2, yg2);
            jg2 = max(xg2, yg2);
            kg2 = (int) ((((kg1 - 1) * 1LL * lk + mk) % (jg2 - ig2 + 1)) + 1);
            ans = incr[get_k(roots[ig2 - 1], roots[jg2], kg2, 1, n)];
            result += ans;
            ig1 = ig2;
            jg1 = jg2;
            kg1 = kg2;
        }
    }
    cout << result << "\n";
 
    return 0;
}