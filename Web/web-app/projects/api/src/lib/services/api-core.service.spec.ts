import { TestBed } from '@angular/core/testing';
import {ApiCoreService} from './api-core.service';


describe('ApiCoreService', () => {
  let service: ApiCoreService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApiCoreService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
