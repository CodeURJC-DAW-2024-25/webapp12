import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateActivityComponent } from './create-activity.component';

describe('CreateActivityComponent', () => {
  let component: CreateActivityComponent;
  let fixture: ComponentFixture<CreateActivityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreateActivityComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CreateActivityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
