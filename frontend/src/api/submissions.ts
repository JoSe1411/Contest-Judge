import { apiClient } from './client';
import type { SubmissionRequest, ExecutionResult } from '../types/models';

export async function submitCode(body: SubmissionRequest): Promise<ExecutionResult> {
  const { data } = await apiClient.post<ExecutionResult>('/submit', body);
  return data;
}




