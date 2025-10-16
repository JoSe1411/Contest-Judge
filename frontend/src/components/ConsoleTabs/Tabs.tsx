import { useState } from 'react';
import type { ReactNode } from 'react';

interface Tab {
  id: 'test' | 'output' | 'debug';
  label: string;
  content: ReactNode;
}

interface Props {
  tabs: Tab[];
  defaultTab?: Tab['id'];
}

export default function Tabs({ tabs, defaultTab = 'test' }: Props) {
  const [active, setActive] = useState<Tab['id']>(defaultTab);
  const current = tabs.find((t) => t.id === active) || tabs[0];
  return (
    <div className="flex flex-col h-full">
      <div className="flex items-center gap-2 border-b border-white/5 px-2 text-sm">
        {tabs.map((t) => (
          <button
            key={t.id}
            onClick={() => setActive(t.id)}
            className={`px-2 py-2 rounded-t-md ${active === t.id ? 'text-white bg-gray-900' : 'text-gray-400 hover:text-gray-200'}`}
          >
            {t.label}
          </button>
        ))}
      </div>
      <div className="p-3 flex-1 overflow-auto thin-scrollbar">{current?.content}</div>
    </div>
  );
}


