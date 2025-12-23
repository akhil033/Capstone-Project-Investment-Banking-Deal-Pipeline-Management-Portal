export enum DealType {
  MA = 'MA',
  IPO = 'IPO',
  Debt = 'Debt',
  Equity = 'Equity',
  Advisory = 'Advisory'
}

export enum DealStage {
  Prospect = 'Prospect',
  UnderEvaluation = 'UnderEvaluation',
  TermSheetSubmitted = 'TermSheetSubmitted',
  Closed = 'Closed',
  Lost = 'Lost'
}

export interface Note {
  userId: string;
  note: string;
  timestamp: string;
}

export interface Deal {
  id: string;
  clientName: string;
  dealType: string;
  sector: string;
  dealValue?: number;
  currentStage: string; // Changed from DealStage to string to handle backend response
  summary: string;
  notes: Note[];
  createdBy: string;
  assignedTo: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateDealRequest {
  clientName: string;
  dealType: string;
  sector: string;
  dealValue: number;
  currentStage: DealStage;
  summary: string;
  assignedTo?: string;
}

export interface UpdateDealRequest {
  clientName?: string;
  dealType?: string;
  sector?: string;
  summary?: string;
  assignedTo?: string;
}

export interface UpdateDealStageRequest {
  stage: DealStage;
}

export interface UpdateDealValueRequest {
  dealValue: number;
}

export interface AddNoteRequest {
  note: string;
}
