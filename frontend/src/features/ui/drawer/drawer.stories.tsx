import type { Meta, StoryObj } from "@storybook/react-webpack5";
import { useState } from "react";
import "../../../global.module.css";
import { Drawer } from "./drawer";

function DrawerPreview() {
  const [isOpen, setIsOpen] = useState(true);

  return (
    <div style={{ minHeight: "30rem", padding: "1rem", position: "relative" }}>
      <button onClick={() => setIsOpen(true)} type="button">
        Open Drawer
      </button>

      <Drawer
        closeLabel="Close drawer"
        isOpen={isOpen}
        onClose={() => setIsOpen(false)}
        subtitle="Example preview for the slide-in drawer."
        title="Course Details"
      >
        <p>
          Lorum ipsum dolor sit amet, consectetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris. Maecenas congue ligula ac quam viverra nec consectetur ante hendrerit. Donec et mollis dolor. Praesent et diam eget libero egestas mattis sit amet vitae augue. Nam tincidunt congue enim, ut porta lorem lacinia consectetur. Donec ut libero sed arcu vehicula ultricies a non tortor. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean ut gravida lorem.
        </p>
      </Drawer>
    </div>
  );
}

const meta: Meta<typeof Drawer> = {
  title: "Features/UI/Drawer",
  component: Drawer,
};

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {
  render: () => <DrawerPreview />,
};
