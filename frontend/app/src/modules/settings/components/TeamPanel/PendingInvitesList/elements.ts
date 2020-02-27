import styled from 'styled-components';

export const StyledPendingInviteList = styled.ul`
  margin: 0px;
  padding: 0px;

  li {
    display: flex;
    border-bottom: 1px solid #e4e8eb;
    padding: 8px;
  }

  li > div:first-child {
    width: 100%;
    max-width: 320px;
  }

  li > div:not(:first-child) {
    width: 100%;
    max-width: 160px;
  }
`;
