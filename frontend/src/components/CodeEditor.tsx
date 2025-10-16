import Editor from '@monaco-editor/react';
import type { Language } from '../types/models';

interface Props {
  language: Language;
  code: string;
  onChange: (next: string) => void;
}

function mapLanguage(lang: Language): string {
  if (lang === 'py') return 'python';
  if (lang === 'cpp') return 'cpp';
  return 'java';
}

export default function CodeEditor({ language, code, onChange }: Props) {
  return (
    <div className="h-full panel overflow-hidden">
      <Editor
        height="100%"
        theme="vs-dark"
        defaultLanguage={mapLanguage(language)}
        language={mapLanguage(language)}
        value={code}
        onChange={(v) => onChange(v ?? '')}
        options={{
          minimap: { enabled: false },
          fontSize: 14,
          scrollBeyondLastLine: false,
          wordWrap: 'on',
          smoothScrolling: true,
          cursorBlinking: 'smooth',
          renderWhitespace: 'selection',
          scrollbar: {
            verticalScrollbarSize: 6,
            horizontalScrollbarSize: 6,
            useShadows: false,
          },
        }}
      />
    </div>
  );
}


