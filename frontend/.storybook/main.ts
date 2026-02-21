import type { StorybookConfig } from '@storybook/react-webpack5';

const config: StorybookConfig = {
  "stories": [
    "../src/**/*.stories.@(js|jsx|mjs|ts|tsx)"
  ],
  "addons": [
    "@storybook/preset-create-react-app"
  ],
  "framework": "@storybook/react-webpack5",
  "staticDirs": [
    "../public"
  ]
};
export default config;