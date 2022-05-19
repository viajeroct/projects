#include <bits/stdc++.h>

using namespace std;

struct Node {
    string value;
    int freq;

    Node *left;
    Node *right;

    Node(const string &value_, int freq_) {
        value = value_;
        freq = freq_;
        left = nullptr;
        right = nullptr;
    }

    Node(const string &value_, int freq_, Node *left_, Node *right_) {
        value = value_;
        freq = freq_;
        left = left_;
        right = right_;
    }
};

bool cmp(const Node *a, const Node *b) {
    return a->freq <= b->freq;
}

Node *createHuffmanTree(const vector<pair<string, int>> &input) {
    set<Node *, decltype(&cmp)> s(&cmp);
    for (const auto &it : input) {
        s.insert(new Node(it.first, it.second));
    }

    while ((int) (s.size()) > 1) {
        auto first = *s.begin();
        s.erase(s.begin());
        auto second = *s.begin();
        s.erase(s.begin());
        s.insert(new Node(first->value + second->value, first->freq + second->freq, first, second));
    }

    return *s.begin();
}

void encode(Node *root, vector<pair<string, string>> &res, const string &code = "") {
    if (root == nullptr) {
        return;
    }
    if (root->left == nullptr && root->right == nullptr) {
        res.emplace_back(root->value, code);
    }
    encode(root->left, res, code + "0");
    encode(root->right, res, code + "1");
}

void pprint(const vector<pair<string, string>> &res) {
    for (const auto &it : res)
        cout << "Huffman code for " << it.first << " is " << it.second << ".\n";
}

vector<pair<string, int>> readInput() {
    int n, freq;
    string str;
    vector<pair<string, int>> ans;
    cout << "Input size of alph: \n";
    cout.flush();
    cin >> n;
    cout << "Input symbol and it's frequency: \n";
    cout.flush();
    for (int i = 0; i < n; i++) {
        cin >> str >> freq;
        ans.emplace_back(str, freq);
    }
    return ans;
}

int main() {
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
    cout.tie(nullptr);

    vector<pair<string, int>> input = readInput();
    vector<pair<string, string>> ans;
    encode(createHuffmanTree(input), ans);
    sort(ans.begin(), ans.end(), [&](const auto &a, const auto &b) {
        if (a.second.size() == b.second.size()) {
            return a.second < b.second;
        }
        return a.second.size() < b.second.size();
    });
    pprint(ans);

    return 0;
}
