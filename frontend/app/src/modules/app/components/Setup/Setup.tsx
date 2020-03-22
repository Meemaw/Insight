import React, { useState } from 'react';
import {
  Navbar,
  NavbarGroup,
  Classes,
  Icon,
  Tabs,
  Tab,
  Tag,
} from '@blueprintjs/core';
import Link from 'next/link';
import styled from 'styled-components';

const StyledTabs = styled(NavbarGroup)`
  flex: 1;
  justify-content: center;

  .bp3-tab-indicator-wrapper {
    margin-top: 10px;
  }
`;

const Setup = () => {
  const [selectedId, setSelectedId] = useState('profile');

  return (
    <div>
      <Navbar className={Classes.DARK} style={{ display: 'flex' }}>
        <NavbarGroup style={{ width: 290 }}>
          <div style={{ margin: '0 15px 0 0' }}>
            <Link href="/">
              <a style={{ color: 'inherit' }}>
                <Icon icon="offline" iconSize={24} />
              </a>
            </Link>
          </div>
        </NavbarGroup>

        <StyledTabs>
          <Tabs
            selectedTabId={selectedId}
            onChange={(newTab) => setSelectedId(newTab as string)}
          >
            <Tab
              id="profile"
              title={
                <div>
                  <Tag round>1</Tag>
                  <span style={{ marginLeft: 8 }}>Create profile</span>
                </div>
              }
            />
            <Tab
              id="account"
              title={
                <div>
                  <Tag round>2</Tag>
                  <span style={{ marginLeft: 8 }}>Set up account</span>
                </div>
              }
            />
            <Tab
              id="recording"
              title={
                <div>
                  <Tag round>3</Tag>
                  <span style={{ marginLeft: 8 }}>Start recording</span>
                </div>
              }
            />
          </Tabs>
        </StyledTabs>

        <NavbarGroup style={{ width: 290, justifyContent: 'flex-end' }}>
          TODO
        </NavbarGroup>
      </Navbar>
    </div>
  );
};

export default Setup;
