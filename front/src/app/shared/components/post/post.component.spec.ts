import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';

import { PostComponent } from './post.component';

describe('PostComponent', () => {
  let component: PostComponent;
  let fixture: ComponentFixture<PostComponent>;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [PostComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PostComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
  });

  it('should navigate to the article detail page when the card is clicked', () => {
    component.article = {
      id: 42,
      title: 'Read me',
      content: 'Some content to show',
      author: { pseudo: 'author' },
    };

    spyOn(router, 'navigate');

    fixture.detectChanges();

    const card = fixture.nativeElement.querySelector('.post-card') as HTMLElement;
    card.click();

    expect(router.navigate).toHaveBeenCalledOnceWith(['/article', 42]);
    expect(fixture.nativeElement.querySelector('a')).toBeNull();
  });

  it('should not navigate when the article has no id', () => {
    component.article = {
      title: 'Read me',
      content: 'Some content to show',
      author: { pseudo: 'author' },
    };

    spyOn(router, 'navigate');

    fixture.detectChanges();

    const card = fixture.nativeElement.querySelector('.post-card') as HTMLElement;
    card.click();

    expect(router.navigate).not.toHaveBeenCalled();
  });
});