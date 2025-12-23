import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { DealService } from './deal.service';
import { Deal, CreateDealRequest, UpdateDealRequest, UpdateDealStageRequest, UpdateDealValueRequest, Note, AddNoteRequest, DealStage } from '../models/deal.model';

describe('DealService', () => {
  let service: DealService;
  let httpMock: HttpTestingController;
  const API_URL = 'http://localhost:8080/api/deals';

  const mockDeal: Deal = {
    id: '1',
    clientName: 'Test Client',
    dealType: 'M&A',
    sector: 'Technology',
    currentStage: 'Prospect',
    dealValue: 1000000,
    summary: 'Test deal',
    assignedTo: 'John Doe',
    createdAt: '2024-01-01T00:00:00',
    updatedAt: '2024-01-01T00:00:00',
    createdBy: 'admin',
    notes: []
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        DealService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(DealService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAllDeals', () => {
    it('should fetch all deals', () => {
      const mockDeals: Deal[] = [mockDeal];

      service.getAllDeals().subscribe(deals => {
        expect(deals).toEqual(mockDeals);
        expect(deals.length).toBe(1);
      });

      const req = httpMock.expectOne(API_URL);
      expect(req.request.method).toBe('GET');
      req.flush(mockDeals);
    });
  });

  describe('getDealById', () => {
    it('should fetch deal by id', () => {
      service.getDealById('1').subscribe(deal => {
        expect(deal).toEqual(mockDeal);
      });

      const req = httpMock.expectOne(`${API_URL}/1`);
      expect(req.request.method).toBe('GET');
      req.flush(mockDeal);
    });
  });

  describe('createDeal', () => {
    it('should create a new deal', () => {
      const createRequest: CreateDealRequest = {
        clientName: 'New Client',
        dealType: 'M&A',
        sector: 'Technology',
        currentStage: DealStage.Prospect,
        dealValue: 1000000,
        summary: 'New deal',
        assignedTo: 'John Doe'
      };

      service.createDeal(createRequest).subscribe(deal => {
        expect(deal.clientName).toBe('New Client');
      });

      const req = httpMock.expectOne(API_URL);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(createRequest);
      req.flush({ ...mockDeal, ...createRequest });
    });
  });

  describe('updateDeal', () => {
    it('should update deal', () => {
      const updateRequest: UpdateDealRequest = {
        clientName: 'Updated Client',
        dealType: 'IPO',
        sector: 'Finance',
        summary: 'Updated summary',
        assignedTo: 'Jane Doe'
      };

      service.updateDeal('1', updateRequest).subscribe(deal => {
        expect(deal.clientName).toBe('Updated Client');
      });

      const req = httpMock.expectOne(`${API_URL}/1`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updateRequest);
      req.flush({ ...mockDeal, ...updateRequest });
    });
  });

  describe('updateDealStage', () => {
    it('should update deal stage', () => {
      const stageRequest: UpdateDealStageRequest = {
        stage: DealStage.UnderEvaluation
      };

      service.updateDealStage('1', stageRequest).subscribe(deal => {
        expect(deal.currentStage).toBe('UnderEvaluation');
      });

      const req = httpMock.expectOne(`${API_URL}/1/stage`);
      expect(req.request.method).toBe('PATCH');
      expect(req.request.body).toEqual(stageRequest);
      req.flush({ ...mockDeal, currentStage: 'UnderEvaluation' });
    });
  });

  describe('updateDealValue', () => {
    it('should update deal value', () => {
      const valueRequest: UpdateDealValueRequest = {
        dealValue: 2000000
      };

      service.updateDealValue('1', valueRequest).subscribe(deal => {
        expect(deal.dealValue).toBe(2000000);
      });

      const req = httpMock.expectOne(`${API_URL}/1/value`);
      expect(req.request.method).toBe('PATCH');
      expect(req.request.body).toEqual(valueRequest);
      req.flush({ ...mockDeal, dealValue: 2000000 });
    });
  });

  describe('deleteDeal', () => {
    it('should delete deal', () => {
      service.deleteDeal('1').subscribe();

      const req = httpMock.expectOne(`${API_URL}/1`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });
  });

  describe('addNote', () => {
    it('should add note to deal', () => {
      const noteRequest: AddNoteRequest = {
        note: 'Test note'
      };

      service.addNote('1', noteRequest).subscribe(deal => {
        expect(deal.notes.length).toBeGreaterThan(0);
      });

      const req = httpMock.expectOne(`${API_URL}/1/notes`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(noteRequest);
      const mockNote: Note = { userId: 'admin', note: 'Test note', timestamp: '2024-01-01T00:00:00' };
      req.flush({ ...mockDeal, notes: [mockNote] });
    });
  });
});
