#include <bits/stdc++.h>
 
using namespace std;
 
struct Tree3D {
    int n;
    vector<vector<vector<int64_t>>> t;
 
    explicit Tree3D(int n) : n(n) {
        t.assign(n, vector<vector<int64_t>>(n, vector<int64_t>(n, 0)));
    }
 
    void add(int x, int y, int z, int delta) {
        for (int i = x; i < n; i += (i + 1) & -(i + 1))
            for (int j = y; j < n; j += (j + 1) & -(j + 1))
                for (int k = z; k < n; k += (k + 1) & -(k + 1))
                    t[i][j][k] += delta;
    }
 
    int64_t sum(int x, int y, int z) {
        int64_t ans = 0;
        for (int i = x; i >= 0; i -= (i + 1) & -(i + 1))
            for (int j = y; j >= 0; j -= (j + 1) & -(j + 1))
                for (int k = z; k >= 0; k -= (k + 1) & -(k + 1))
                    ans += t[i][j][k];
        return ans;
    }
};
 
int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);
 
    int n;
    cin >> n;
    Tree3D t = Tree3D(n);
    while (true) {
        int cmd;
        cin >> cmd;
        if (cmd == 1) {
            int x, y, z;
            int delta;
            cin >> x >> y >> z;
            cin >> delta;
            t.add(x, y, z, delta);
        } else if (cmd == 2) {
            int x1, y1, z1;
            cin >> x1 >> y1 >> z1;
            int x2, y2, z2;
            cin >> x2 >> y2 >> z2;
            int64_t ans;
            int64_t value1 = t.sum(x2, y2, z2) - t.sum(x1 - 1, y2, z2) - t.sum(x2, y1 - 1, z2) +
                             t.sum(x1 - 1, y1 - 1, z2);
            int64_t value2 = t.sum(x2, y2, z1 - 1) - t.sum(x1 - 1, y2, z1 - 1) - t.sum(x2, y1 - 1, z1 - 1) +
                             t.sum(x1 - 1, y1 - 1, z1 - 1);
            ans = value1 - value2;
            cout << ans << "\n";
        } else if (cmd == 3) {
            return 0;
        } else {
            assert(false);
        }
    }
}