import { apiClient } from './client';
import type { RunCompareRequest, RunCompareResult } from '../types/models';

export async function runCompare(body: RunCompareRequest): Promise<RunCompareResult> {
  const { data } = await apiClient.post<RunCompareResult>('/run-compare', body);
  return data;
}




