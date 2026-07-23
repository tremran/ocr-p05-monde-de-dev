import { Component, DestroyRef, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Observable, catchError, of, tap } from 'rxjs';
import { Topic, TopicService } from '../../services/topic.service';

@Component({
    selector: 'app-legal-mentions',
    templateUrl: './legal-mentions.component.html',
    styleUrls: ['./legal-mentions.component.scss'],
    standalone: false
})
export class LegalMentionsComponent {

}