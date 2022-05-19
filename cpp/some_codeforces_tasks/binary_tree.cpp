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
 
struct Node {
    int64_t x, y;
    Node *l, *r;
 
    explicit Node(int64_t x_) : x(x_) {
        y = randomNumbers.getNext();
        l = r = nullptr;
    }
};
 
void split(Node *root, Node *&l, Node *&r, int64_t x) {
    if (!root) {
        l = r = nullptr;
        return;
    }
    if (root->x < x) {
        split(root->r, root->r, r, x);
        l = root;
    } else {
        split(root->l, l, root->l, x);
        r = root;
    }
}
 
void merge(Node *&root, Node *l, Node *r) {
    if (!l || !r) {
        if (!r) root = l;
        else root = r;
        return;
    }
    if (l->y > r->y) {
        merge(l->r, l->r, r);
        root = l;
    } else {
        merge(r->l, l, r->l);
        root = r;
    }
}
 
void insert(Node *&root, Node *v) {
    if (!root) {
        root = v;
        return;
    }
    if (root->y > v->y) {
        if (v->x < root->x) insert(root->l, v);
        else insert(root->r, v);
    } else {
        split(root, v->l, v->r, v->x);
        root = v;
    }
}
 
void remove(Node *&root, int64_t x) {
    if (!root) {
        return;
    }
    if (x < root->x) {
        remove(root->l, x);
    } else if (x > root->x) {
        remove(root->r, x);
    } else {
        merge(root, root->l, root->r);
    }
}
 
bool contains(Node *&root, int64_t x) {
    if (!root) {
        return false;
    }
    if (x == root->x) {
        return true;
    } else if (x < root->x) {
        return contains(root->l, x);
    } else {
        return contains(root->r, x);
    }
}
 
Node *prev(Node *&root, int64_t x) {
    Node *cur = root;
    Node *ans = nullptr;
    while (cur) {
        if (cur->x < x) {
            ans = cur;
            cur = cur->r;
        } else {
            cur = cur->l;
        }
    }
    return ans;
}
 
Node *next(Node *&root, int64_t x) {
    Node *cur = root;
    Node *ans = nullptr;
    while (cur) {
        if (cur->x > x) {
            ans = cur;
            cur = cur->l;
        } else {
            cur = cur->r;
        }
    }
    return ans;
}
 
int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);
 
    string s;
    int64_t x;
    Node *root = nullptr;
 
    while (cin >> s >> x) {
        if (s == "insert") {
            if (!contains(root, x)) {
                insert(root, new Node(x));
            }
        } else if (s == "exists") {
            if (contains(root, x)) cout << "true";
            else cout << "false";
            cout << "\n";
        } else if (s == "delete") {
            if (contains(root, x)) {
                remove(root, x);
            }
        } else if (s == "next") {
            Node *v = next(root, x);
            if (!v) cout << "none";
            else cout << v->x;
            cout << "\n";
        } else if (s == "prev") {
            Node *v = prev(root, x);
            if (!v) cout << "none";
            else cout << v->x;
            cout << "\n";
        }
    }
 
    return 0;
}
