#include <bits/stdc++.h>
 
using namespace std;
 
#define int long long
 
string encode(const string &s) {
    int n = (int) (s.length());
    int last_bit = 1, special_bits = 0;
    while (true) {
        if (((last_bit) & (last_bit - 1)) == 0) {
            special_bits++;
        }
        if (last_bit - special_bits == n) {
            break;
        }
        last_bit++;
    }
    vector<int> code(last_bit);
    int position = 0;
    for (int i = 0; i < last_bit; i++) {
        int num = i + 1;
        if (((num) & (num - 1)) == 0) {
            code[i] = 0;
        } else {
            code[i] = s[position++] - '0';
        }
    }
    for (int step = 0; (1LL << step) <= last_bit; step++) {
        int cur_step = (1LL << step);
        int cur_bit = 0;
        for (int i = 0; i < last_bit; i++) {
            int num = i + 1;
            if ((num >> step) & 1) {
                cur_bit ^= code[i];
            }
        }
        code[cur_step - 1] = cur_bit;
    }
    string ans;
    for (const int &it : code) {
        ans += (char) (it + '0');
    }
    return ans;
}
 
string decode(const string &s) {
    int n = (int) (s.length());
    vector<int> code(n);
    for (int i = 0; i < n; i++) {
        code[i] = s[i] - '0';
    }
    int sum = 0;
    for (int step = 0; (1LL << step) <= n; step++) {
        int cur_step = (1LL << step);
        int cur_bit = 0;
        for (int i = 0; i < n; i++) {
            int num = i + 1;
            if ((num >> step) & 1) {
                cur_bit ^= code[i];
            }
        }
        if (cur_bit != 0) {
            sum += cur_step;
        }
    }
    if (sum != 0) {
        code[sum - 1] = 1 - code[sum - 1];
    }
    string ans;
    for (int i = 0; i < n; i++) {
        int num = i + 1;
        if (((num) & (num - 1)) != 0) {
            ans += (char) (code[i] + '0');
        }
    }
    return ans;
}
 
signed main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);
 
    int cmd;
    cin >> cmd;
    string s;
    cin >> s;
    if (cmd == 1) {
        cout << encode(s) << "\n";
    } else if (cmd == 2) {
        cout << decode(s) << "\n";
    } else {
        assert(false);
    }
 
    return 0;
}
