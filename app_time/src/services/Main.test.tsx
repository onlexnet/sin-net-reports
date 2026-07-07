import React from 'react';
import { ServicesDefault } from './index';

// Mock Redux and complex dependencies
jest.mock('react-redux', () => ({
  connect: () => () => 'MockedComponent',
  ConnectedProps: {},
}));

jest.mock('react-router-dom', () => ({
  RouteComponentProps: {},
}));

jest.mock('../api/useDownloadFile', () => ({
  useDownloadFile: jest.fn(() => ({
    downloadFile: jest.fn(),
    loading: false,
    error: null,
    data: null,
  })),
}));

describe('ServicesDefault', () => {
  it('should be defined and importable', () => {
    expect(ServicesDefault).toBeDefined();
    expect(ServicesDefault).toBeTruthy();
  });

  it('should handle basic validation without rendering', () => {
    // Test that the component export exists
    expect(ServicesDefault).not.toBeNull();
    expect(ServicesDefault).not.toBeUndefined();
  });
});