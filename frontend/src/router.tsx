import { createBrowserRouter } from 'react-router-dom';
import ProblemPage from './pages/ProblemPage';

export const router = createBrowserRouter([
  {
    path: '/problems/:questionId',
    element: <ProblemPage />,
  },
  {
    path: '/',
    element: <ProblemPage />, // default to a problem page for now
  },
]);


