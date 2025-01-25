import { TestBed } from '@angular/core/testing';

import { TotalStatsService } from './total-stats.service';

describe('TotalStatsService', () => {
  let service: TotalStatsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TotalStatsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
