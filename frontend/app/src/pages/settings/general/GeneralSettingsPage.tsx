/* eslint-disable react/jsx-wrap-multilines */
import React, { useState } from 'react';
import authenticated from 'hoc/auth';
import AppLayout from 'components/AppLayout';
import { Tabs, Tab, Icon } from '@blueprintjs/core';
import dynamic from 'next/dynamic';

const LazySetupPanel = dynamic(() => import('components/settings/SetupPanel'));
const LazyTeamPanel = dynamic(() => import('components/settings/TeamPanel'));

const GeneralSettings = () => {
  const [selectedTabId, setSelectedTabId] = useState<React.ReactText>(
    '/settings/general'
  );

  const handleTabChange = (newTabId: React.ReactText) => {
    setSelectedTabId(newTabId);
  };

  return (
    <AppLayout>
      <div style={{ background: '#F9F9F9', height: '100%' }}>
        <Tabs
          vertical
          onChange={handleTabChange}
          selectedTabId={selectedTabId}
          renderActiveTabPanelOnly
        >
          <Tab
            id="/settings/general"
            title={
              <>
                <Icon icon="build" />
                Setup Insight
              </>
            }
            panel={<LazySetupPanel />}
          />
          <Tab
            id="/settings/tracking"
            title={
              <>
                <Icon icon="record" />
                Tracking
              </>
            }
            panel={<div>Tracking</div>}
          />
          <Tab
            id="/settings/team"
            title={
              <>
                <Icon icon="people" />
                Team
              </>
            }
            panel={<LazyTeamPanel />}
          />
        </Tabs>
      </div>
    </AppLayout>
  );
};

export default authenticated(GeneralSettings);
