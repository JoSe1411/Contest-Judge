interface Props {
  debug?: string | null;
}

export default function DebugTab({ debug }: Props) {
  if (!debug) return <div className="text-sm text-gray-400">No debug output</div>;
  return <pre className="p-2 text-sm bg-gray-900 border border-gray-800 rounded whitespace-pre-wrap">{debug}</pre>;
}




