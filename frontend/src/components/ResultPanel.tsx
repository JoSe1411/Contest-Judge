import type { ExecutionResult } from '../types/models';
import classNames from 'classnames';

interface Props {
  result?: ExecutionResult | null;
  isLoading?: boolean;
  errorText?: string | null;
}

export default function ResultPanel({ result, isLoading, errorText }: Props) {
  if (isLoading) {
    return <div className="p-3 text-sm text-gray-300">Running...</div>;
  }

  if (errorText) {
    return (
      <div className="p-3 text-sm bg-red-900/40 border border-red-800 rounded text-red-200">
        {errorText}
      </div>
    );
  }

  if (!result) {
    return <div className="p-3 text-sm text-gray-400">No test cases run yet</div>;
  }

  const bannerClass = classNames('px-3 py-2 rounded text-sm mb-3', {
    'bg-green-900/40 border border-green-800 text-green-200': result.passed,
    'bg-red-900/40 border border-red-800 text-red-200': !result.passed,
  });

  return (
    <div>
      <div className={bannerClass}>
        {result.passed ? 'Test case passed' : 'Test case failed'}
      </div>
      <div className="grid grid-cols-2 gap-3">
        <div className="border border-gray-800 rounded">
          <div className="px-2 py-1 text-xs bg-gray-900 border-b border-gray-800">Output</div>
          <pre className="p-2 text-sm whitespace-pre-wrap">{result.output}</pre>
        </div>
        <div className="border border-gray-800 rounded">
          <div className="px-2 py-1 text-xs bg-gray-900 border-b border-gray-800">Expected</div>
          <pre className="p-2 text-sm whitespace-pre-wrap">{result.expected}</pre>
        </div>
      </div>
      <div className="mt-2 text-xs text-gray-400">exitCode: {result.exitCode}</div>
      {result.error && (
        <pre className="mt-2 p-2 text-xs bg-gray-900 border border-gray-800 rounded text-red-300 whitespace-pre-wrap">{result.error}</pre>
      )}
    </div>
  );
}


