import type { Problem } from '../../types/models';

interface Props {
  problem?: Problem | null;
  customInput: string;
  onChangeCustomInput: (v: string) => void;
}

export default function TestCasesTab({ problem, customInput, onChangeCustomInput }: Props) {
  return (
    <div className="space-y-3">
      <div>
        <div className="text-xs text-gray-400 mb-1">Sample cases</div>
        {problem?.samples?.length ? (
          <ul className="space-y-1 text-sm">
            {problem.samples.map((s, i) => (
              <li key={i} className="border border-gray-800 rounded p-2">
                <div className="text-xs text-gray-400 mb-1">Input</div>
                <pre className="whitespace-pre-wrap">{s.input}</pre>
                <div className="text-xs text-gray-400 mt-2 mb-1">Expected</div>
                <pre className="whitespace-pre-wrap">{s.output}</pre>
              </li>
            ))}
          </ul>
        ) : (
          <div className="text-sm text-gray-400">No sample cases</div>
        )}
      </div>
      <div>
        <div className="text-xs text-gray-400 mb-1">Custom input</div>
        <textarea
          className="w-full h-32 bg-gray-900 border border-gray-800 rounded p-2 text-sm"
          value={customInput}
          onChange={(e) => onChangeCustomInput(e.target.value)}
          placeholder="Type input fed to stdin"
        />
      </div>
    </div>
  );
}


