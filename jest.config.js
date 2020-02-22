module.exports = {
  roots: ['<rootDir>/src'],
  transform: {
    '.ts': 'ts-jest',
  },
  testEnvironment: 'node',
  coveragePathIgnorePatterns: ['/node_modules/'],
  setupFilesAfterEnv: ['<rootDir>/src/test/setup.ts'],
  moduleDirectories: ['node_modules', 'src'],
};
