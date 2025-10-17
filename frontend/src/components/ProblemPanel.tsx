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
      <div className="text-3xl font-semibold text-gray-100 mb-4 text-center">Problem Description</div>
      <div className="prose prose-invert max-w-none pl-6">
        <h3 className="mt-0 text-2xl font-bold text-gray-50 mb-4">{problem.title}</h3>
        <div className="text-lg text-gray-200 leading-relaxed">
          <ReactMarkdown remarkPlugins={[remarkGfm]}>{problem.descriptionMarkdown}</ReactMarkdown>
        </div>
        {problem.samples?.length ? (
          <div className="mt-6">
            <h4 className="text-xl font-semibold text-gray-50 mb-4">Example Cases</h4>
            {problem.samples.map((s, i) => (
              <div key={i} className="mb-4 bg-gray-900/50 p-4 rounded-lg border border-gray-800">
                <div className="text-sm font-medium text-emerald-400 mb-2">Input</div>
                <pre className="text-base text-gray-100 whitespace-pre-wrap bg-gray-950 p-3 rounded">{s.input}</pre>
                <div className="text-sm font-medium text-blue-400 mt-3 mb-2">Output</div>
                <pre className="text-base text-gray-100 whitespace-pre-wrap bg-gray-950 p-3 rounded">{s.output}</pre>
              </div>
            ))}
          </div>
        ) : null}
      </div>
    </div>
  );
}


