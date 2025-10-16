export type Language = 'cpp' | 'java' | 'py';

export interface SubmissionRequest {
  userId: string;
  questionId: string;
  language: Language;
  code: string;
}

export interface ExecutionResult {
  exitCode: number;
  output: string;
  expected: string;
  passed: boolean;
  error: string | null;
}

export interface RunCompareRequest extends SubmissionRequest {
  input: string;
}

export type RunCompareResult = ExecutionResult;

export interface ProblemSample {
  input: string;
  output: string;
}

export interface Problem {
  id: string;
  title: string;
  descriptionMarkdown: string;
  samples?: ProblemSample[];
}

