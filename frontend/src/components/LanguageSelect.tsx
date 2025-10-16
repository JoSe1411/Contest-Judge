import type { Language } from '../types/models';

interface Props {
  value: Language;
  onChange: (lang: Language) => void;
}

const options: { value: Language; label: string }[] = [
  { value: 'py', label: 'Python 3' },
  { value: 'cpp', label: 'C++17' },
  { value: 'java', label: 'Java 17' },
];

export default function LanguageSelect({ value, onChange }: Props) {
  return (
    <select
      className="bg-gray-800 border border-gray-700 rounded px-2 py-1 text-sm"
      value={value}
      onChange={(e) => onChange(e.target.value as Language)}
    >
      {options.map((o) => (
        <option key={o.value} value={o.value}>{o.label}</option>
      ))}
    </select>
  );
}


