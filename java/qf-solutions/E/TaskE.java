import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;

public class TaskE {
    static int n, m;
    static Vertex[] gr;
    static int[] d, parent;

    public static void main(String[] args) throws IOException {
        FastScanner in = new FastScanner();

        n = in.nextInt();
        m = in.nextInt();
        gr = new Vertex[n];
        for (int i = 0; i < n; i++) {
            gr[i] = new Vertex();
        }
        for (int i = 0; i < n - 1; i++) {
            int u = in.nextInt() - 1, v = in.nextInt() - 1;
            gr[u].adj.add(v);
            gr[v].adj.add(u);
        }
        int team = -1;
        for (int i = 0; i < m; i++) {
            int cur = in.nextInt() - 1;
            gr[cur].isTeam = true;
            if (team == -1) {
                team = cur;
            }
        }

        d = new int[n];
        parent = new int[n];
        Arrays.fill(d, -1);
        Arrays.fill(parent, -1);
        DFS(team, 0);

        int vertex = -1;
        for (int i = 0; i < n; i++) {
            if (vertex == -1) {
                if (gr[i].isTeam) {
                    vertex = i;
                }
            } else {
                if (gr[i].isTeam && d[i] > d[vertex]) {
                    vertex = i;
                }
            }
        }

        IntList path = new IntList();
        while (vertex != -1) {
            path.add(vertex);
            vertex = parent[vertex];
        }

        IntList centers = new IntList();
        centers.add(path.get(path.size() / 2));
        if (path.size() / 2 + 1 < path.size()) {
            centers.add(path.get(path.size() / 2 + 1));
        }
        if (path.size() / 2 - 1 >= 0) {
            centers.add(path.get(path.size() / 2 - 1));
        }

        for (int i = 0; i < centers.size(); i++) {
            int center = centers.get(i);
            if (tryCenter(center)) {
                System.out.println("YES");
                System.out.println(center + 1);
                return;
            }
        }
        System.out.println("NO");
    }

    static boolean tryCenter(int center) {
        Arrays.fill(d, -1);
        DFS(center, 0);
        int common = -1;
        for (int i = 0; i < n; i++) {
            if (!gr[i].isTeam) {
                continue;
            }
            if (common == -1) {
                common = d[i];
            } else {
                if (common != d[i]) {
                    return false;
                }
            }
        }
        return true;
    }

    static void DFS(int v, int depth) {
        d[v] = depth;
        IntList cur = gr[v].adj;
        for (int i = 0; i < cur.size(); i++) {
            int to = cur.get(i);
            if (d[to] == -1) {
                parent[to] = v;
                DFS(to, depth + 1);
            }
        }
    }

    static class Vertex {
        IntList adj;
        boolean isTeam;

        public Vertex() {
            adj = new IntList();
            isTeam = false;
        }
    }

    static public class IntList {
        private int size;
        private int[] dt;

        public IntList() {
            size = 0;
            dt = new int[1];
        }

        public int get(int index) {
            return dt[index];
        }

        public void add(int value) {
            if (size >= dt.length) {
                dt = Arrays.copyOf(dt, dt.length * 2);
            }
            dt[size] = value;
            size++;
        }

        public int size() {
            return size;
        }
    }

    static class FastScanner {
        Reader in;

        boolean EOF;
        char[] buffer;
        int position, size;
        char ch;

        FastScanner() {
            in = new InputStreamReader(System.in);
            init();
        }

        void init() {
            position = size = 0;
            buffer = new char[1024];
            EOF = false;
        }

        void getNextSymbol() throws IOException {
            if (position == size) {
                size = in.read(buffer);
                position = 0;
            }
            if (size == -1) {
                EOF = true;
                return;
            }
            ch = buffer[position++];
        }

        public int nextInt() throws IOException {
            return Integer.parseInt(next());
        }

        boolean isWord(char ch) {
            return Character.isDigit(ch);
        }

        boolean isEOF() {
            return !EOF;
        }

        void skipSpaces() throws IOException {
            while (!isWord(ch) && isEOF()) {
                getNextSymbol();
            }
        }

        String next() throws IOException {
            StringBuilder ans = new StringBuilder();
            skipSpaces();
            if (!isWord(ch)) {
                return null;
            }
            do {
                ans.append(ch);
                getNextSymbol();
            } while (isWord(ch) && isEOF());
            return ans.toString();
        }
    }
}
