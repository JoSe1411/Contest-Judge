import { useEffect, useMemo, useState } from 'react';
import { useParams } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { languageTemplates } from '../constants/templates';
import type { ExecutionResult, Language, Problem } from '../types/models';
import LanguageSelect from '../components/LanguageSelect';
import CodeEditor from '../components/CodeEditor';
import Tabs from '../components/ConsoleTabs/Tabs';
import TestCasesTab from '../components/ConsoleTabs/TestCasesTab';
import OutputTab from '../components/ConsoleTabs/OutputTab';
import DebugTab from '../components/ConsoleTabs/DebugTab';
import ProblemPanel from '../components/ProblemPanel';
import { useRunCompare } from '../hooks/useRunCompare';
import { useSubmitCode } from '../hooks/useSubmitCode';

const qc = new QueryClient();

async function fetchProblem(id: string): Promise<Problem> {
  const res = await fetch(`/problems/${id}`);
  return res.json();
}

export default function ProblemPage() {
  const { questionId = 'two-sum' } = useParams();
  const [problem, setProblem] = useState<Problem | null>(null);
  const [language, setLanguage] = useState<Language>('py');
  const [code, setCode] = useState<string>(languageTemplates.py);
  const [customInput, setCustomInput] = useState<string>('');
  const [userId] = useState<string>('demo-user');

  const runCompareMutation = useRunCompare();
  const submitMutation = useSubmitCode();

  useEffect(() => {
    fetchProblem(questionId).then(setProblem).catch(() => setProblem(null));
  }, [questionId]);

  useEffect(() => {
    setCode(languageTemplates[language]);
  }, [language]);

  const runResult: ExecutionResult | null = runCompareMutation.data ?? null;
  const runError = runCompareMutation.isError ? (runCompareMutation.error as Error).message : null;

  const submitResult: ExecutionResult | null = submitMutation.data ?? null;
  const submitError = submitMutation.isError ? (submitMutation.error as Error).message : null;

  const outputTabContent = useMemo(() => {
    return (
      <OutputTab
        result={runResult || submitResult}
        isLoading={runCompareMutation.isPending || submitMutation.isPending}
        errorText={runError || submitError}
      />
    );
  }, [runResult, submitResult, runCompareMutation.isPending, submitMutation.isPending, runError, submitError]);

  function onRun() {
    runCompareMutation.mutate({ userId, questionId, language, code, input: customInput });
  }

  function onSubmit() {
    submitMutation.mutate({ userId, questionId, language, code });
  }

  return (
    <QueryClientProvider client={qc}>
      <div className="min-h-screen bg-gray-900 flex items-center justify-center p-8">
        <div className="w-full max-w-7xl h-[90vh]">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 h-full">
            <div className="panel p-6 overflow-auto max-h-full">
              <ProblemPanel problem={problem} />
            </div>
            <div className="flex flex-col overflow-hidden gap-3">
              <div className="panel px-3 py-3 flex items-center justify-between">
                <div className="text-sm font-medium">{problem?.title || 'Problem'}</div>
                <LanguageSelect value={language} onChange={setLanguage} />
              </div>
              <div className="h-[50vh] min-h-[350px]">
                <CodeEditor language={language} code={code} onChange={setCode} />
              </div>
              <div className="panel min-h-[300px] flex flex-col">
                <Tabs
                  tabs={[
                    { id: 'test', label: 'Test Cases', content: (
                      <TestCasesTab problem={problem} customInput={customInput} onChangeCustomInput={setCustomInput} />
                    ) },
                    { id: 'output', label: 'Output', content: outputTabContent },
                    { id: 'debug', label: 'Debug', content: <DebugTab debug={(runResult?.error || submitResult?.error) ?? null} /> },
                  ]}
                />
                <div className="px-3 py-3 border-t border-white/5 flex items-center gap-3 justify-end">
                  <button onClick={onRun} disabled={runCompareMutation.isPending} className="btn-cta-ghost disabled:opacity-60">Run Code</button>
                  <button onClick={onSubmit} disabled={submitMutation.isPending} className="btn-cta disabled:opacity-60">Submit</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </QueryClientProvider>
  );
}


