import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import { worker } from './mocks/browser'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'

const queryClient = new QueryClient()

async function prepare() {
  const useMocks = import.meta.env.VITE_USE_MOCKS !== 'false';
  if (useMocks) {
    await worker.start({ serviceWorker: { url: '/mockServiceWorker.js' } });
  }

  createRoot(document.getElementById('root')!).render(
    <StrictMode>
      <QueryClientProvider client={queryClient}>
        <App />
      </QueryClientProvider>
    </StrictMode>,
  )
}

prepare()
