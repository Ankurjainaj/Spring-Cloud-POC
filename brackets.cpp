#include<bits/stdc++.h>
using namespace std;

struct Node{
    int pairs;
    int open;
    int closed;

    Node(){
        pairs=open=closed=0;
    }
};

class SegTree{
    public:
    vector<Node> tree;
    int n;
    string s;

    SegTree(string str)
    {
        s = str;
        n = str.size();
        tree.resize(4*n);
        build(1, 0, n-1);
    }

    Node merge(Node left, Node right)
    {
        Node res;
        int mini = min(left.open, right.closed);
        res.pairs = left.pairs + right.pairs + mini;
        res.open = left.open + right.open - mini;
        res.closed = left.closed + right.closed - mini;
        return res;
    }

    void build(int index, int l, int r)
    {
        if(l==r)
        {
            if(s[l]=='(') tree[index].open=1;
            else tree[index].closed=1;
        }

        int mid = (l+r)/2;
        build(2*index, l, mid);
        build(2*index+1, mid+1, r);
        tree[index] = merge(tree[2*index], tree[2*index+1]);
    }

    Node query(int index, int low, int high, int l, int r)
    {
        if(low>r || high<l)
        {
            return Node();
        }
        else if(r<=low && l>=high)
        {
            return tree[index];
        }

        int mid = (l+r)/2;
        Node left = query(2*index, low, mid, l, r);
        Node right = query(2*index+1, mid+1, high, l, r);
        tree[index] = merge(left, right);
    }
};
