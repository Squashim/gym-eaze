import { afterEach, vi } from 'vitest';
import { cleanup } from '@testing-library/react';
import '@testing-library/jest-dom/vitest';

function mockMatchMedia() {
  Object.defineProperty(window, 'matchMedia', {
    value: vi.fn(() => ({ matches: true, addListener: vi.fn(), removeListener: vi.fn() })),
  });
}

class ResizeObserver {
  observe() {}
  unobserve() {}
  disconnect() {}
}
Object.defineProperty(window, 'ResizeObserver', {
  writable: true,
  configurable: true,
  value: ResizeObserver,
});

beforeAll(() => {
  mockMatchMedia();
});

afterEach(() => {
  cleanup();
  vi.clearAllMocks();
});
