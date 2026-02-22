import type { Preview } from '@storybook/react-webpack5'
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { Story } from 'storybook/internal/csf';

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
};

export default preview;