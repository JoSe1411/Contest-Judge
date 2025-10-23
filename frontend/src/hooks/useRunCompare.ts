import { useMutation } from '@tanstack/react-query';
import { runCompare } from '../api/runs';
import type { RunCompareRequest, RunCompareResult } from '../types/models';

export function useRunCompare() {
  return useMutation<RunCompareResult, Error, RunCompareRequest>({
    mutationFn: (body) => runCompare(body),
  });
}




