/* eslint-disable react/jsx-wrap-multilines */
import React, { useState } from 'react';
import authenticated from 'modules/auth/hoc/authenticated';
import AppLayout from 'modules/app/components/Layout';
import { Tabs, Tab, Icon, Colors } from '@blueprintjs/core';
import dynamic from 'next/dynamic';
import styled from 'styled-components';
import { BaseRouter } from 'next/dist/next-server/lib/router/router';

type Props = {
  url: BaseRouter;
};

const StyledGeneralSettingsSection = styled.div`
  background: ${Colors.DARK_GRAY4};
  height: 100%;

  .bp3-tab-list {
    margin-top: 32px;
  }

  .bp3-tab-panel {
    width: 100%;
    padding: 32px !important;
  }
`;

const LazySetupPanel = dynamic(() =>
  import('modules/settings/components/SetupPanel')
);
const LazyTeamPanel = dynamic(() =>
  import('modules/settings/components/TeamPanel')
);

const GeneralSettings = ({ url }: Props) => {
  const [selectedTabId, setSelectedTabId] = useState<React.ReactText>(
    '/settings/general'
  );

  const handleTabChange = (newTabId: React.ReactText) => {
    setSelectedTabId(newTabId);
  };

  return (
    <AppLayout pathname={url.pathname}>
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
                <span>Setup Insight</span>
              </>
            }
            panel={<LazySetupPanel />}
          />
          <Tab
            id="/settings/tracking"
            title={
              <div style={{ padding: 12 }}>
                <Icon icon="record" />
                <span style={{ marginLeft: 12 }}>Tracking</span>
              </div>
            }
            panel={<div>Tracking</div>}
          />
          <Tab
            id="/settings/team"
            title={
              <>
                <Icon icon="people" />
                <span>Team</span>
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
