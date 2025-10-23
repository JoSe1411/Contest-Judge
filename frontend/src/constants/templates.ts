import type { Language } from '../types/models';

export const languageTemplates: Record<Language, string> = {
  py: `# Python 3
def main():
    import sys
    data = sys.stdin.read()
    # TODO: implement
    print(data.strip())

if __name__ == '__main__':
    main()
`,
  cpp: `// C++17
#include <bits/stdc++.h>
using namespace std;
int main(){
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    string s; 
    getline(cin, s);
    cout << s << "\n";
    return 0;
}
`,
  java: `// Java 17
import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String s = br.readLine();
        System.out.println(s);
    }
}
`,
};




