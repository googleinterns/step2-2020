import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HardcodedComponent } from './hardcoded.component';

describe('HardcodedComponent', () => {
  let component: HardcodedComponent;
  let fixture: ComponentFixture<HardcodedComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HardcodedComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HardcodedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
