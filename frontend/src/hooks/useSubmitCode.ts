import { useMutation } from '@tanstack/react-query';
import { submitCode } from '../api/submissions';
import type { SubmissionRequest, ExecutionResult } from '../types/models';

export function useSubmitCode() {
  return useMutation<ExecutionResult, Error, SubmissionRequest>({
    mutationFn: (body) => submitCode(body),
  });
}




