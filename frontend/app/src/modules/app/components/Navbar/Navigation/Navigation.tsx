import React from 'react';
import { IconName, Tabs, Tab, Tooltip, Icon } from '@blueprintjs/core';
import Link from 'next/link';

const NAVIGATION_ITEMS = [
  { to: '/', tooltipContent: 'Home', icon: 'home' as IconName },
  {
    to: '/insights',
    tooltipContent: 'Insights',
    icon: 'predictive-analysis' as IconName,
  },
];

type NavigationItem = typeof NAVIGATION_ITEMS[number];

type Props = {
  pathname: string;
  items?: NavigationItem[];
};

const Navigation = ({ pathname, items = NAVIGATION_ITEMS }: Props) => {
  return (
    <Tabs selectedTabId={pathname}>
      {items.map(({ to, tooltipContent, icon }) => {
        return (
          <Tab
            key={to}
            id={to}
            title={
              <Link href={to}>
                <a>
                  <Tooltip content={tooltipContent}>
                    <Icon icon={icon} />
                  </Tooltip>
                </a>
              </Link>
            }
          />
        );
      })}
    </Tabs>
  );
};

export default React.memo(Navigation);
