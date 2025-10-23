import ResultPanel from '../ResultPanel';
import type { ExecutionResult } from '../../types/models';

interface Props {
  result?: ExecutionResult | null;
  isLoading?: boolean;
  errorText?: string | null;
}

export default function OutputTab(props: Props) {
  return <ResultPanel {...props} />;
}




