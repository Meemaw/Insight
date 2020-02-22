/* eslint-disable react/jsx-wrap-multilines */
import React, { useState } from 'react';
import authenticated from 'modules/auth/hoc/authenticated';
import AppLayout from 'components/common/app/Layout';
import { Tabs, Tab, Icon } from '@blueprintjs/core';
import dynamic from 'next/dynamic';

import { StyledGeneralSettingsSection } from './elements';

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
      <StyledGeneralSettingsSection>
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
      </StyledGeneralSettingsSection>
    </AppLayout>
  );
};

export default authenticated(GeneralSettings);
