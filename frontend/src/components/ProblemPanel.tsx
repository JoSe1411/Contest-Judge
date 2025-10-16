import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import type { Problem } from '../types/models';

interface Props {
  problem?: Problem | null;
}

export default function ProblemPanel({ problem }: Props) {
  if (!problem) {
    return <div className="text-sm text-gray-400">Loading problem...</div>;
  }
  return (
    <div className="space-y-4">
      <h2 className="text-xl font-semibold text-gray-100 mb-4">Problem Description</h2>
      <div className="prose prose-invert max-w-none">
        <h3 className="mt-0 text-lg">{problem.title}</h3>
        <ReactMarkdown remarkPlugins={[remarkGfm]}>{problem.descriptionMarkdown}</ReactMarkdown>
        {problem.samples?.length ? (
          <div>
            <h4 className="text-base">Example Cases</h4>
            {problem.samples.map((s, i) => (
              <div key={i} className="mb-3">
                <div className="text-xs text-gray-400">Input</div>
                <pre className="whitespace-pre-wrap">{s.input}</pre>
                <div className="text-xs text-gray-400 mt-2">Output</div>
                <pre className="whitespace-pre-wrap">{s.output}</pre>
              </div>
            ))}
          </div>
        ) : null}
      </div>
    </div>
  );
}


