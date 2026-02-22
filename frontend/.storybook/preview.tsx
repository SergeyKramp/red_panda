import type { Preview } from '@storybook/react-webpack5'
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { initialize, mswLoader } from 'msw-storybook-addon'

// Initialize MSW
initialize()

const queryClient = new QueryClient();

const preview: Preview = {
  parameters: {
    controls: {
      matchers: {
       color: /(background|color)$/i,
       date: /Date$/i,
      },
    },
  },
  decorators: [
        (Story) => (
            <QueryClientProvider client={queryClient}>
               <Story/> 
            </QueryClientProvider>
    ),
  ],
  // Provide the MSW addon loader globally
  loaders: [mswLoader],
};

export default preview;