import { http, HttpResponse } from 'msw';
import type { ExecutionResult, Problem, RunCompareRequest, SubmissionRequest } from '../types/models';

const problems: Record<string, Problem> = {
  'two-sum': {
    id: 'two-sum',
    title: 'Two Sum Problem',
    descriptionMarkdown: `Solve the classic Two Sum.

Input: array and target.
Output: indices whose values sum to target.
`,
    samples: [
      { input: '2 7 11 15\n9', output: '0 1' },
    ],
  },
};

export const handlers = [
  http.get('/problems/:id', ({ params }) => {
    const id = params.id as string;
    const problem = problems[id] || problems['two-sum'];
    return HttpResponse.json(problem);
  }),

  http.post('/run-compare', async ({ request }) => {
    const body = (await request.json()) as RunCompareRequest;

    // Simple mock: expected is reversed input, output echoes code length to vary results
    const expected = body.input.trim();
    const output = body.input.trim();
    const passed = output === expected;
    const result: ExecutionResult = {
      exitCode: 0,
      output,
      expected,
      passed,
      error: null,
    };
    return HttpResponse.json(result);
  }),

  http.post('/submit', async ({ request }) => {
    await request.json() as SubmissionRequest;
    const result: ExecutionResult = {
      exitCode: 0,
      output: '42',
      expected: '42',
      passed: true,
      error: null,
    };
    return HttpResponse.json(result);
  }),
];


